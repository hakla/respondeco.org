package business.accounts

import java.time.LocalDateTime

import javax.inject.Inject
import anorm._
import business.organisations.{OrganisationExists, OrganisationService, OrganisationWriteModel}
import common.Database
import persistence.Queries
import security.{Role, User}

import scala.util.{Failure, Try}

class AccountService @Inject()(val organisationService: OrganisationService, implicit val db: Database) extends Queries[AccountModel] {

    import common.DateImplicits._

    implicit val parser: RowParser[AccountModel] =
        SqlParser.get[Long]("id") ~
            SqlParser.get[String]("email") ~
            SqlParser.get[String]("name") ~
            SqlParser.get[Option[String]]("password") ~
            SqlParser.get[Option[Long]]("organisation") ~
            SqlParser.get[Option[String]]("image") ~
            Role.parser map {
            case id ~ email ~ name ~ pwd ~ organisation ~ image ~ role => AccountModel(id, email, name, pwd.getOrElse("leer"), role, organisation, image)
        }

    val table: String = "account"

    /**
      * Admin creates a new account
      */
    def createByAdmin(account: AccountWriteModel): Option[AccountModel] = {
        create(account.email, account.name, account.password, account.organisationId)
    }

    /**
      * Registration by the user
      */
    def createFromRegistration(account: RegistrationModel): AccountModel = db.withConnection { implicit connection =>
        val organisation = organisationService.first('name -> account.name)
        val user = first('name -> account.name)

        validateRegistration(account) {
            val createdAccount = organisationService.create(OrganisationWriteModel(
                name = account.name
            )).flatMap(organisation =>
                create(account.email, account.name, Some(account.password), Some(organisation.id))
            )

            createdAccount.getOrElse(throw new AccountCreationFailed)
        }
    }

    def create(email: String, name: String, password: Option[String], organisationId: Option[Long], image: Option[String] = None): Option[AccountModel] = db.withConnection { implicit connection =>
        import security.Password.StringExtensions

        SQL(s"insert into $table (email, name, organisation, password, role, image) values({email}, {name}, {organisationId}, {password}, {role}, {image})").on(
            'email -> email,
            'name -> name,
            'organisationId -> organisationId,
            'password -> password.map(_.hashedPassword),
            'role -> User.value,
            'image -> image
        ).executeInsert().asInstanceOf[Option[Long]].map { id =>
            AccountModel(id, email, name, password.getOrElse(""), User, organisationId, image)
        }
    }

    def changePassword(id: Long, changeRequest: PasswordChangeRequest): Boolean = db.withConnection { implicit connection =>
        import security.Password.StringExtensions

        byId(id).exists(account =>
            if (changeRequest.oldPassword.verifyPassword(account.password))
                SQL(s"update $table set password = {password} where id = {id}").on(
                    'id -> id,
                    'password -> changeRequest.newPassword.hashedPassword
                ).executeUpdate() == 1
            else false
        )
    }

    def updateByUser(id: Long, account: AccountWriteModel): Option[AccountModel] = db.withConnection { implicit c =>
        // User cannot change the password this way (has to use changePassword)
        update(id, account.copy(
            password = None
        ))
    }

    def update(id: Long, account: AccountWriteModel): Option[AccountModel] = db.withConnection { implicit c =>
        import security.Password.StringExtensions

        account.password.map(password =>
            SQL(s"update $table set email = {email}, name = {name}, organisation = {organisationId}, password = {password}, role = {role}, image = {image}, updatedAt = CURRENT_TIMESTAMP where id = {id}").on(
                'id -> id,
                'email -> account.email,
                'name -> account.name,
                'organisationId -> account.organisationId,
                'password -> password.hashedPassword,
                'role -> Role.getRole(account.role).value,
                'image -> account.image
            )
        ).getOrElse(
            SQL(s"update $table set email = {email}, name = {name}, organisation = {organisationId}, role = {role}, image = {image}, updatedAt = CURRENT_TIMESTAMP where id = {id}").on(
                'id -> id,
                'email -> account.email,
                'name -> account.name,
                'organisationId -> account.organisationId,
                'role -> Role.getRole(account.role).value,
                'image -> account.image
            )
        ).executeUpdate() match {
            case 1 => byId(id)
            case _ => None
        }
    }

    def findByEmail(email: String): Option[AccountModel] = first('email -> email)

    private def validateRegistration(registrationModel: RegistrationModel)(block: => AccountModel): AccountModel = db.withConnection { implicit connection =>
        val rowParser: RowParser[Option[Int] ~ Option[Int] ~ Option[Int]] =
            SqlParser.get[Option[Int]]("organisation") ~
                SqlParser.get[Option[Int]]("account_name") ~
                SqlParser.get[Option[Int]]("account_email")

        val registrationChecks = SQL(
            s"""
               | select
               | (select 1 from organisation where lower(name) = {name}) as organisation,
               | (select 1 from account where lower(name) = {name}) as account_name,
               | (select 1 from account where lower(email) = {email}) as account_email
               | from dual
           """.stripMargin
        ).on(
            'email -> registrationModel.email.toLowerCase,
            'name -> registrationModel.name.toLowerCase
        ).as(rowParser.single)

        if (registrationChecks._1._1.isDefined) throw new OrganisationExists
        else if (registrationChecks._1._2.isDefined) throw new AccountWithNameExists
        else if (registrationChecks._2.isDefined) throw new AccountWithEmailExists
        else block
    }

    def toPublicModel(accountModel: AccountModel): AccountPublicModel = {
        AccountPublicModel.from(accountModel).copy(
            organisationImage = accountModel.organisationId
                .flatMap(organisationService.byId)
                .map(organisation => organisation.logo.getOrElse(""))
        )
    }

}

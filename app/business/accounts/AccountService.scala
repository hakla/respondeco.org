package business.accounts

import javax.inject.Inject

import anorm._
import common.Database
import persistence.Queries
import security.{Role, User}

class AccountService @Inject()(implicit val db: Database) extends Queries[AccountModel] {

    implicit val parser: RowParser[AccountModel] =
        SqlParser.get[Long]("id") ~
            SqlParser.get[String]("email") ~
            SqlParser.get[String]("name") ~
            SqlParser.get[String]("password") ~
            SqlParser.get[Option[Long]]("organisation") ~
            Role.parser map {
            case id ~ email ~ name ~ pwd ~ organisation ~ role => AccountModel(id, email, name, pwd, role, organisation)
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
    def createFromRegistration(account: RegistrationModel, organisationId: Option[Long]): Option[AccountModel] = {
        create(account.email, account.name, Some(account.password), organisationId)
    }

    def create(email: String, name: String, password: Option[String], organisationId: Option[Long]): Option[AccountModel] = db.withConnection { implicit connection =>
        import security.Password.StringExtensions

        SQL(s"insert into $table (email, name, organisation, password, role) values({email}, {name}, {organisationId}, {password}, {role})").on(
            'email -> email,
            'name -> name,
            'organisationId -> organisationId,
            'password -> password.map(_.hashedPassword),
            'role -> User.value
        ).executeInsert().asInstanceOf[Option[Long]].map { id =>
            AccountModel(id, email, name, password.getOrElse(""), User, organisationId)
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
            SQL(s"update $table set email = {email}, name = {name}, organisation = {organisationId}, password = {password}, role = {role} where id = {id}").on(
                'id -> id,
                'email -> account.email,
                'name -> account.name,
                'organisationId -> account.organisationId,
                'password -> password.hashedPassword,
                'role -> Role.getRole(account.role).value
            )
        ).getOrElse(
            SQL(s"update $table set email = {email}, name = {name}, organisation = {organisationId}, role = {role} where id = {id}").on(
                'id -> id,
                'email -> account.email,
                'name -> account.name,
                'organisationId -> account.organisationId,
                'role -> Role.getRole(account.role).value
            )
        ).executeUpdate() match {
            case 1 => byId(id)
            case _ => None
        }
    }

    def findByEmail(email: String): Option[AccountModel] = first('email -> email)

}

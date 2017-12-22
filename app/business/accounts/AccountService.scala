package business.accounts

import javax.inject.Inject

import anorm._
import business.organisations.{OrganisationService, OrganisationWriteModel}
import common.Database
import persistence.Queries
import security.{Role, User}

class AccountService @Inject()(implicit val db: Database, val organisationService: OrganisationService) extends Queries[AccountModel] {

    implicit val parser: RowParser[AccountModel] =
        SqlParser.get[Long]("id") ~
            SqlParser.get[String]("email") ~
            SqlParser.get[String]("name") ~
            SqlParser.get[String]("password") ~
            SqlParser.get[Long]("organisation") ~
            Role.parser map {
            case id ~ email ~ name ~ pwd ~ organisation ~ role => AccountModel(id, email, name, pwd, role, organisation)
        }

    val table: String = "account"

    def create(account: RegistrationModel): Option[AccountModel] = {
        create(account.email, account.name, account.password, account.organisation)
    }

    def create(email: String, name: String, password: String, organisationName: String): Option[AccountModel] = db.withConnection { implicit connection =>
        SQL(s"insert into $table (email, name, password, role) values({email}, {name}, {password}, {role})").on(
            'email -> email,
            'name -> name,
            'password -> password,
            'role -> User.value
        ).executeInsert().asInstanceOf[Option[Long]].map { id =>
            // Account has been created, now we create the organisation for it
            organisationService.create(OrganisationWriteModel(
                name = organisationName
            )).map(organisation => {
                AccountModel(id, email, name, password, User, organisation.id)
            }).get
            // TODO .get isn't safe
        }
    }

    def update(id: Long, account: AccountWriteModel): Option[AccountModel] = db.withConnection { implicit c =>
        SQL(s"update $table set email = {email}, name = {name} where id = {id}").on(
            'id -> id,
            'email -> account.email,
            'name -> account.name
        ).executeUpdate() match {
            case 1 => byId(id)
            case _ => None
        }
    }

    def findByEmail(email: String): Option[AccountModel] = first('email -> email)

}

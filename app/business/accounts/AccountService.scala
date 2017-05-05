package business.accounts

import javax.inject.Inject

import anorm._
import common.{CrudService, Database}
import security.{Role, User}

class AccountService @Inject()(implicit val db: Database) extends CrudService[AccountModel, AccountWriteModel] {

    implicit val parser: RowParser[AccountModel] =
        SqlParser.get[Long]("id") ~
            SqlParser.get[String]("email") ~
            SqlParser.get[String]("name") ~
            SqlParser.get[String]("password") ~
            Role.parser map {
            case id ~ email ~ name ~ pwd ~ role => AccountModel(id, email, name, pwd, role)
        }

    val table: String = "account"

    def create(account: AccountWriteModel): Option[AccountModel] = {
        if (account.password.isDefined)
            create(account.email, account.name, account.password.get)
        else
            None
    }

    def create(email: String, name: String, password: String): Option[AccountModel] = db.withConnection { implicit connection =>
        SQL(s"insert into $table (email, name, password, role) values({email}, {name}, {password}, {role})").on(
            'email -> email,
            'name -> name,
            'password -> password,
            'role -> User.value
        ).executeInsert().asInstanceOf[Option[Long]].map { id =>
            AccountModel(id, email, name, password, User)
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

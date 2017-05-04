package business.accounts

import javax.inject.Inject

import anorm._
import common.Database
import persistence.Queries
import security.{Role, User}

/**
  * Created by Clemens Puehringer on 28/11/15.
  */
class AccountService @Inject()(implicit val db: Database) extends Queries[Account] {

    implicit val table: String = "account"
    implicit val parser: RowParser[Account] =
        SqlParser.get[Long]("id") ~
            SqlParser.get[String]("email") ~
            SqlParser.get[String]("name") ~
            SqlParser.get[String]("password") ~
            Role.parser map {
            case id ~ email ~ name ~ pwd ~ role => Account(id, email, name, pwd, role)
        }

    def create(account: AccountNew): Option[Account] = {
        create(account.email, account.name, account.password)
    }

    def create(email: String, name: String, password: String): Option[Account] = db.withConnection { implicit connection =>
        SQL(s"insert into $table (email, name, password, role) values({email}, {name}, {password}, {role})").on(
            'email -> email,
            'name -> name,
            'password -> password,
            'role -> User.value
        ).executeInsert().asInstanceOf[Option[Long]].map { id =>
            Account(id, email, name, password, User)
        }
    }

    def findByEmail(email: String): Option[Account] = first('email -> email)

    def update(id: Long, account: AccountPublic): Option[Account] = db.withConnection { implicit c =>
        SQL(s"update $table set email = {email}, name = {name} where id = {id}").on(
            'id -> id,
            'email -> account.email,
            'name -> account.name
        ).executeUpdate() match {
            case 1 => byId(id)
            case _ => None
        }
    }


}

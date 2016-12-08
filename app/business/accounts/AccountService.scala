package business.accounts

import java.sql.Connection
import javax.inject.Inject

import anorm._
import authentication.{Role, User}
import common.Database
import persistence.Queries

/**
  * Created by Clemens Puehringer on 28/11/15.
  */
class AccountService @Inject()(implicit val db: Database) extends Queries[Account] {

    implicit val parser: RowParser[Account] = {
        SqlParser.get[Long]("id") ~
            SqlParser.get[String]("email") ~
            SqlParser.get[String]("password") ~
            Role.parser map {
            case id ~ email ~ pwd ~ role => Account(id, email, pwd, role)
        }
    }

    def create(account: AccountNew): Option[Account] = {
        create(account.email, account.password)
    }

    def create(email: String, password: String): Option[Account] = {
        db.withConnection { implicit connection =>
            SQL(s"insert into $table (email, password, role) " +
                "values({email}, {password}, {role})").on(
                'email -> email,
                'password -> password,
                'role -> User.value
            ).executeInsert().asInstanceOf[Option[Long]].map { id =>
                Account(id, email, password, User)
            }
        }
    }

    def findByEmail(email: String): Option[Account] = {
        db.withConnection { implicit c =>
            first(
                'email -> email
            )
        }
    }

    def update(id: Long, account: AccountPublic): Option[Account] = {
        db.withConnection { implicit c =>
            SQL(s"update $table set email = {email} where id = {id}").on(
                'id -> id,
                'email -> account.email
            ).executeUpdate() match {
                case 1 => byId(id)
                case _ => None
            }
        }
    }

}

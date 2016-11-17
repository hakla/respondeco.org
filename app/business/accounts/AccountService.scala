package business.accounts

import java.sql.Connection
import javax.inject.Inject

import anorm.{RowParser, SqlParser, _}
import authentication.{Role, User}
import common.Database
import persistence.Queries

/**
  * Created by Clemens Puehringer on 28/11/15.
  */
class AccountService @Inject()(db: Database) {

    val parser: RowParser[Account] = {
        SqlParser.get[Long]("id") ~
            SqlParser.get[String]("email") ~
            SqlParser.get[String]("password") ~
            Role.parser map {
            case id ~ email ~ pwd ~ role => Account(id, email, pwd, role)
        }
    }
    val queries = new Queries[Account]("Account", parser)

    def create(account: AccountNew): Option[Account] = {
        create(account.email, account.password)
    }

    def create(email: String, password: String): Option[Account] = {
        db.withConnection { implicit connection =>
            SQL("insert into Account (email, password, role) " +
                "values({email}, {password}, {role})").on(
                'email -> email,
                'password -> password,
                'role -> User.value
            ).executeInsert().asInstanceOf[Option[Long]].map { id =>
                Account(id, email, password, User)
            }
        }
    }

    def findById(id: Long): Option[Account] = {
        db.withConnection { implicit connection =>
            SQL("SELECT * FROM Account WHERE id = {id}").on(
                'id -> id
            ).executeQuery().as(parser.*) match {
                case x :: _ => Some(x)
                case _ => None
            }
        }
    }

    def findByEmail(email: String): Option[Account] = {
        db.withConnection { implicit c =>
            SQL("select * from Account where email = {email}").on(
                'email -> email
            ).executeQuery().as(parser.*) match {
                case acc :: _ => Some(acc)
                case _ => None
            }
        }
    }

    def findAll(): List[Account] = {
        db.withConnection { implicit c =>
            SQL("SELECT * FROM Account").executeQuery().as(parser.*)
        }
    }

    def update(id: Long, account: AccountPublic): Option[Account] = {
        db.withConnection { implicit c =>
            SQL("update Account set email = {email} where id = {id}").on(
                'id -> id,
                'email -> account.email
            ).executeUpdate() match {
                case 1 => findById(id)
                case _ => None
            }
        }
    }

}

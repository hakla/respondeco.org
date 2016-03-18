package models

import java.sql.Connection

import anorm._
import authentication.{User, Role}
import persistence.Queries
import play.api.db.DB
import play.api.Play.current
import play.api.libs.json.Json
import play.db.Database

import scala.concurrent.Future

/**
  * Created by Clemens Puehringer on 28/11/15.
  */
case class Account(id: Long, email: String, password: String, organizationId: Long, role: Role)

case class AccountNew(email : String, password : String, organizationName : String)

case class AccountPublic(id: Long, email: String, organizationId: Long)

object Account {

    val parser: RowParser[Account] = {
        SqlParser.get[Long]("id") ~
        SqlParser.get[String]("email") ~
        SqlParser.get[String]("password") ~
        SqlParser.get[Long]("organizationId") ~
        Role.parser map {
            case id~email~pwd~orgId~role => Account(id,email,pwd,orgId,role)
        }
    }
    val queries = new Queries[Account]("Account", parser)

    def create(email: String, password: String, organizationId: Long)(implicit connection: Connection):
    Option[Account] = {
        SQL("insert into Account (email, password, organizationId, role) " +
            "values({email}, {password}, {organizationId}, {role})").on(
                'email -> email,
                'password -> password,
                'organizationId -> organizationId,
                'role -> User.value
        ).executeInsert().asInstanceOf[Option[Long]].map { id =>
            Account(id, email, password, organizationId, User)
        }
    }

    def findById(id: Long): Option[Account] = {
        DB.withConnection { implicit connection =>
            SQL("SELECT * FROM Account WHERE id = {id}").on(
                'id -> id
            ).executeQuery().as(parser.*) match {
                case x::_ => Some(x)
                case _ => None
            }
        }
    }

    def findByEmail(email: String): Option[Account] = {
        DB.withConnection { implicit c =>
            SQL("select * from Account where email = {email}").on(
                'email -> email
            ).executeQuery().as(parser.*) match {
                case acc::_ => Some(acc)
                case _ => None
            }
        }
    }

}

object AccountNew {
    implicit val formatter = Json.format[AccountNew]
}
object AccountPublic {
    implicit val formatter = Json.format[AccountPublic]

    def from(a: Account): AccountPublic = {
        AccountPublic(a.id, a.email, a.organizationId)
    }
}
//
//object AccountUpdate {
//
//    implicit val formatter = Json.format[AccountUpdate]
//
//}

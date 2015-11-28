package models

import java.sql.Connection

import anorm._
import persistence.Queries
import play.api.db.DB
import play.api.Play.current
import play.api.libs.json.Json

/**
  * Created by Clemens Puehringer on 28/11/15.
  */
case class Account(id: Long, email: String, password: String, organizationId: Long)

case class AccountInsert(email: String, password: String, organizationId: Long)

case class AccountCreate(email: String, password: String, organizationName: String)

case class AccountUpdate(id: Long, password: String)

//class for public viewing, without password or other sensitive data
case class AccountPublic(id: Long, email: String, organizationId: Long)


object Account {

    implicit val formatter = Json.format[Account]

    val parser: RowParser[AccountPublic] = Macro.namedParser[AccountPublic]
    val queries = new Queries[AccountPublic]("Account", parser)

    def create(account: AccountInsert)(implicit connection: Connection): Option[AccountPublic] = {
        SQL("insert into Account (email, password, organizationId) values({email}, {password}, {organizationId})").on(
            'email -> account.email,
            'password -> account.password,
            'organizationId -> account.organizationId
        ).executeInsert().asInstanceOf[Option[Long]].map { id =>
            AccountPublic(id, account.email, account.organizationId)
        }
    }

    def update(account: AccountUpdate): Option[AccountPublic] = DB.withConnection { implicit connection =>
        SQL("update Account set password = {password} where id = {id}").on(
            'id -> account.id,
            'password -> account.password
        ).executeUpdate().asInstanceOf[Option[Long]] match {
            case Some(count) => Account.findById(account.id)
            case None => None
        }
    }

    def findAll(): List[AccountPublic] = DB.withConnection { implicit connection =>
        SQL("select * from Account").executeQuery().as(parser.*)
    }

    def findById(id: Long): Option[AccountPublic] = DB.withConnection { implicit connection =>
        SQL("SELECT * FROM Account WHERE id = {id}").on(
            'id -> id
        ).executeQuery().as(parser.*) match {
            case x::_ => Some(x)
            case _ => None
        }
    }

    def findByEmail(email: String): Option[AccountPublic] = DB.withConnection { implicit c =>
        SQL("select * from Account where email = {email}").on(
            'email -> email
        ).executeQuery().as(parser.*) match {
            case acc::_ => Some(acc)
            case _ => None
        }
    }

}

object AccountPublic {

    implicit val formatter = Json.format[AccountPublic]

}

object AccountCreate {

    implicit val formatter = Json.format[AccountCreate]

}

object AccountUpdate {

    implicit val formatter = Json.format[AccountUpdate]

}

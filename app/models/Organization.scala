package models

import java.sql.Connection
import javax.inject.Inject

import anorm._
import persistence.Queries
import play.api.Play.current
import play.api.db.{DB, DBApi}
import play.api.libs.json.Json

case class Organization(id: Long, name: String)

case class OrganizationInsert(name: String)

object OrganizationInsert {

    implicit val formatter = Json.format[OrganizationInsert]

}

object Organization {

    def findAll() = DB.withConnection { implicit c =>
        SQL("select * from Organization").executeQuery().as(parser.*)
    }


    implicit val formatter = Json.format[Organization]

    val parser: RowParser[Organization] = Macro.namedParser[Organization]
    val queries = new Queries[Organization]("Organization", parser)

    def findById(id: Long): Option[Organization] = DB.withConnection { implicit c =>
        SQL("SELECT * FROM Organization WHERE id = {id}").on(
            'id -> id
        ).executeQuery().as(parser.*) match {
            case x::_ => Some(x)
            case _ => None
        }
    }

    def create(organization: OrganizationInsert) : Option[Organization] = DB.withConnection { implicit c =>
        SQL("insert into Organization (name) values({name})").on(
        'name -> organization.name
        ).executeInsert().asInstanceOf[Option[Long]].map { id =>
            Organization(id, organization.name)
        }
    }

    def update(id: Long, organization: OrganizationInsert): Option[Organization] = DB.withConnection { implicit c =>
        SQL("update Organization set name = {name} where id = {id}").on(
            'id -> id,
            'name -> organization.name
        ).executeUpdate() match {
            case 1 => Organization.findById(id)
            case _ => None
        }
    }

    def findByName(name: String): Option[Organization] = DB.withConnection { implicit c =>
        SQL("select * from Organization where name = {name}").on(
            'name -> name
        ).executeQuery().as(parser.*) match {
            case org::_ => Some(org)
            case _ => None
        }
    }


}

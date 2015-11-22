package models

import anorm._
import play.api.Play.current
import play.api.db.DB
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

    def findById(id: Long): Organization = DB.withConnection { implicit c =>
        SQL("select * from Organization where id = {id}").on(
        'id -> id
        ).as(parser.single)
    }

    def insert(organization: OrganizationInsert) = DB.withConnection { implicit c =>
        SQL("insert into Organization (name) values({name})").on(
        'name -> organization.name
        ).executeInsert().asInstanceOf[Option[Long]].map { id =>
            Organization(id, organization.name)
        }
    }

    def update(organization: Organization) = DB.withConnection { implicit c =>
        SQL("update Organization set name = {name} where id = {id}").on(
            'id -> organization.id,
            'name -> organization.name
        ).executeUpdate() match {
            case 0 => Organization.findById(organization.id)
            case _ => organization
        }
    }

}

package business.organisations

import javax.inject.Inject

import anorm.{Macro, RowParser, _}
import common.Database
import persistence.Queries

/**
  * Created by Klaus on 17.11.2016.
  */
class OrganisationService @Inject() (db: Database) {

    def findAll() = db.withConnection { implicit c =>
        SQL("select * from Organization").executeQuery().as(parser.*)
    }

    val parser: RowParser[Organisation] = Macro.namedParser[Organisation]
    val queries = new Queries[Organisation]("Organization", parser)

    def findById(id: Long): Option[Organisation] = db.withConnection { implicit c =>
        SQL("SELECT * FROM Organization WHERE id = {id}").on(
            'id -> id
        ).executeQuery().as(parser.*) match {
            case x::_ => Some(x)
            case _ => None
        }
    }

    def create(organization: OrganisationInsert) : Option[Organisation] = db.withConnection { implicit c =>
        SQL("insert into Organization (name) values({name})").on(
            'name -> organization.name
        ).executeInsert().asInstanceOf[Option[Long]].map { id =>
            Organisation(id, organization.name)
        }
    }

    def update(id: Long, organization: OrganisationInsert): Option[Organisation] = db.withConnection { implicit c =>
        SQL("update Organization set name = {name} where id = {id}").on(
            'id -> id,
            'name -> organization.name
        ).executeUpdate() match {
            case 1 => findById(id)
            case _ => None
        }
    }

    def findByName(name: String): Option[Organisation] = db.withConnection { implicit c =>
        SQL("select * from Organization where name = {name}").on(
            'name -> name
        ).executeQuery().as(parser.*) match {
            case org::_ => Some(org)
            case _ => None
        }
    }

}

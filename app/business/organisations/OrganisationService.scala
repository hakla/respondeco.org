package business.organisations

import javax.inject.Inject

import anorm.{Macro, RowParser, _}
import common.Database
import persistence.Queries

/**
  * Created by Klaus on 17.11.2016.
  */
class OrganisationService @Inject() (implicit val db: Database) extends Queries[Organisation] {

    implicit val parser: RowParser[Organisation] = Macro.namedParser[Organisation]

    def create(organization: OrganisationInsert) : Option[Organisation] = db.withConnection { implicit c =>
        SQL(s"insert into $table (name) values({name})").on(
            'name -> organization.name
        ).executeInsert().asInstanceOf[Option[Long]].map { id =>
            Organisation(id, organization.name)
        }
    }

    def update(id: Long, organization: OrganisationInsert): Option[Organisation] = db.withConnection { implicit c =>
        SQL(s"update $table set name = {name} where id = {id}").on(
            'id -> id,
            'name -> organization.name
        ).executeUpdate() match {
            case 1 => byId(id)
            case _ => None
        }
    }

    def findByName(name: String): Option[Organisation] = db.withConnection { implicit c =>
        first('name -> name)
    }

}

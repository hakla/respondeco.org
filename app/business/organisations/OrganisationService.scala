package business.organisations

import javax.inject.Inject

import anorm.{Macro, RowParser, _}
import common.Database
import persistence.Queries

import scala.util.{Failure, Try}

/**
  * Created by Klaus on 17.11.2016.
  */
class OrganisationService @Inject() (implicit val db: Database) extends Queries[Organisation] {

    implicit val parser: RowParser[Organisation] = Macro.namedParser[Organisation]

    def create(organisation: OrganisationInsert) : Option[Organisation] = db.withConnection { implicit c =>
        SQL(s"insert into $table (name, description, email, website, location, category, subcategory) values({name}, {description}, {email}, {website}, {location}, {category}, {subcategory})").on(
            'name -> organisation.name,
            'description -> organisation.description,
            'email -> organisation.email,
            'website -> organisation.website,
            'location -> organisation.location,
            'category -> organisation.category,
            'subcategory -> organisation.subcategory
        ).executeInsert().asInstanceOf[Option[Long]].map { id =>
            Organisation(
                id = id,
                name = organisation.name,
                description = organisation.description,
                email = organisation.email,
                website = organisation.website,
                location = organisation.location,
                category = organisation.category,
                subcategory = organisation.subcategory
            )
        }
    }

    def update(id: Long, organisation: OrganisationInsert): Option[Organisation] = db.withConnection { implicit c =>
        SQL(s"update $table set name = {name}, description = {description}, email = {email}, website = {website}, location = {location}, category = {category}, subcategory = {subcategory} where id = {id}").on(
            'id -> id,
            'name -> organisation.name,
            'description -> organisation.description,
            'email -> organisation.email,
            'website -> organisation.website,
            'location -> organisation.location,
            'category -> organisation.category,
            'subcategory -> organisation.subcategory
        ).executeUpdate() match {
            case 1 => byId(id)
            case _ => None
        }
    }

    def findByName(name: String): Option[Organisation] = db.withConnection { implicit c =>
        first('name -> name)
    }

    def delete(id: Long): Boolean = db.withConnection { implicit c =>
        SQL(s"delete from $table where id = {id}").on(
            'id -> id
        ).executeUpdate() == 1
    }

}

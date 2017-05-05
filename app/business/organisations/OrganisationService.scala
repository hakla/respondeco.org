package business.organisations

import javax.inject.Inject

import anorm.{Macro, RowParser, _}
import common.{CrudService, Database}
import persistence.Queries

import scala.util.{Failure, Try}

/**
  * Created by Klaus on 17.11.2016.
  */
class OrganisationService @Inject() (implicit val db: Database) extends CrudService[OrganisationModel, OrganisationWriteModel] {

    implicit val parser: RowParser[OrganisationModel] = Macro.namedParser[OrganisationModel]
    val table: String = "organisation"

    def create(organisation: OrganisationWriteModel) : Option[OrganisationModel] = db.withConnection { implicit c =>
        SQL(s"insert into $table (name, description, email, website, location, category, subcategory) values({name}, {description}, {email}, {website}, {location}, {category}, {subcategory})").on(
            'name -> organisation.name,
            'description -> organisation.description,
            'email -> organisation.email,
            'website -> organisation.website,
            'location -> organisation.location,
            'category -> organisation.category,
            'subcategory -> organisation.subcategory
        ).executeInsert().asInstanceOf[Option[Long]].map { byId } getOrElse None
    }

    def update(id: Long, organisation: OrganisationWriteModel): Option[OrganisationModel] = db.withConnection { implicit c =>
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

    def findByName(name: String): Option[OrganisationModel] = db.withConnection { implicit c =>
        first('name -> name)
    }

}

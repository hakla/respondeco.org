package business.organisations

import javax.inject.Inject

import anorm.{Macro, RowParser, _}
import common.{CrudService, Database}
import persistence.{ImageService, Queries}
import se.digiplant.res.api.Res

import scala.util.{Failure, Try}

/**
  * Created by Klaus on 17.11.2016.
  */
class OrganisationService @Inject() (implicit val db: Database, val res: Res) extends CrudService[OrganisationModel, OrganisationWriteModel] with ImageService[OrganisationModel, OrganisationWriteModel] {

    implicit val parser: RowParser[OrganisationModel] = Macro.namedParser[OrganisationModel]
    val table: String = "organisation"

    def create(organisation: OrganisationWriteModel) : Option[OrganisationModel] = db.withConnection { implicit c =>
        SQL(s"insert into $table (name, description, email, website, location, category, subcategory, image) values({name}, {description}, {email}, {website}, {location}, {category}, {subcategory}, {image})").on(
            'name -> organisation.name,
            'description -> organisation.description,
            'email -> organisation.email,
            'website -> organisation.website,
            'location -> organisation.location,
            'category -> organisation.category,
            'subcategory -> organisation.subcategory,
            'image -> organisation.image
        ).executeInsert().asInstanceOf[Option[Long]].map { byId } getOrElse None
    }

    def update(id: Long, organisation: OrganisationWriteModel): Option[OrganisationModel] = db.withConnection { implicit c =>
        // remove old image (if there was one)
        byId(id).flatMap { _.image } map { image => res.delete(image) }

        SQL(s"update $table set name = {name}, description = {description}, email = {email}, website = {website}, location = {location}, category = {category}, subcategory = {subcategory}, image = {image} where id = {id}").on(
            'id -> id,
            'name -> organisation.name,
            'description -> organisation.description,
            'email -> organisation.email,
            'website -> organisation.website,
            'location -> organisation.location,
            'category -> organisation.category,
            'subcategory -> organisation.subcategory,
            'image -> organisation.image
        ).executeUpdate() match {
            case 1 => byId(id)
            case _ => None
        }
    }

    def findByName(name: String): Option[OrganisationModel] = db.withConnection { implicit c =>
        first('name -> name)
    }

}

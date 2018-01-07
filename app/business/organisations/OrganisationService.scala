package business.organisations

import java.time.{LocalDateTime, ZoneOffset}
import javax.inject.Inject

import anorm.{Macro, RowParser, _}
import common.{CrudService, Database}
import persistence.{ImageService, Queries}
import se.digiplant.res.api.Res

import scala.util.{Failure, Try}

/**
  * Created by Klaus on 17.11.2016.
  */
class OrganisationService @Inject()(implicit val db: Database, val res: Res) extends CrudService[OrganisationModel, OrganisationWriteModel] with ImageService[OrganisationModel, OrganisationWriteModel] {

    import common.DateImplicits._

    implicit val parser: RowParser[OrganisationModel] = Macro.namedParser[OrganisationModel]
    val table: String = "organisation"

    def create(organisation: OrganisationWriteModel): Option[OrganisationModel] = db.withConnection { implicit c =>
        SQL(s"insert into $table (name, description, email, website, location, category, subcategory, image, logo, video, verified) values({name}, {description}, {email}, {website}, {location}, {category}, {subcategory}, {image}, {logo}, {video}, {verified}, {verifiedAt})").on(
            'name -> organisation.name,
            'description -> organisation.description,
            'email -> organisation.email,
            'website -> organisation.website,
            'location -> organisation.location,
            'category -> organisation.category,
            'subcategory -> organisation.subcategory,
            'image -> organisation.image,
            'logo -> organisation.logo,
            'video -> organisation.video,
            'verified -> organisation.verified,
            'verifiedAt -> getVerificationTime(organisation).flatMap(_.formatted)
        ).executeInsert().asInstanceOf[Option[Long]].flatMap(byId)
    }

    def update(id: Long, organisation: OrganisationWriteModel): Option[OrganisationModel] = db.withConnection { implicit c =>
        SQL(s"update $table set name = {name}, description = {description}, email = {email}, website = {website}, location = {location}, category = {category}, subcategory = {subcategory}, image = {image}, logo = {logo}, video = {video}, verified = {verified}, verifiedAt = {verifiedAt}, updatedAt = CURRENT_TIMESTAMP where id = {id}").on(
            'id -> id,
            'name -> organisation.name,
            'description -> organisation.description,
            'email -> organisation.email,
            'website -> organisation.website,
            'location -> organisation.location,
            'category -> organisation.category,
            'subcategory -> organisation.subcategory,
            'image -> organisation.image,
            'logo -> organisation.logo,
            'video -> organisation.video,
            'verified -> organisation.verified,
            'verifiedAt -> getVerificationTime(organisation).flatMap(_.formatted)
        ).executeUpdate() match {
            case 1 => byId(id)
            case _ => None
        }
    }

    private def getVerificationTime(organisation: OrganisationWriteModel) = {
        // Organisation is marked as verified
        organisation.verified.flatMap(_ =>
            // Check if it currently wasn't verified
            organisation.id.flatMap(id =>
                // And if so set the date to now
                if (!isVerified(id)) {
                    Some(LocalDateTime.now())
                } else {
                    // Otherwise return the value it had before
                    organisation.verifiedAt
                }
            )
        ).orElse(organisation.verifiedAt)
    }

    def findByName(name: String): Option[OrganisationModel] = db.withConnection { implicit c =>
        first('name -> name)
    }

    def isVerified(id: Long): Boolean = db.withConnection { implicit c =>
        SQL(s"SELECT verified FROM $table WHERE id = {id}").on(
            'id -> id
        ).executeQuery().as(
            SqlParser.bool("verified").single
        )
    }

}

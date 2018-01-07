package business.organisations

import java.time.LocalDateTime

import common.{IdModel, MyWriteable}
import play.api.libs.json.{Json, OFormat}

case class OrganisationModel(
    id: Long,
    name: String,
    description: Option[String],
    email: Option[String],
    website: Option[String],
    location: Option[String],
    category: Option[String],
    subcategory: Option[String],
    image: Option[String],
    logo: Option[String],
    video: Option[String],
    verified: Option[Boolean],
    verifiedAt: Option[LocalDateTime]
) extends IdModel

object OrganisationModel extends MyWriteable[OrganisationModel] {
    implicit val formatter: OFormat[OrganisationModel] = Json.format[OrganisationModel]

    def fromWriteModel(id: Long, organisation: OrganisationWriteModel): OrganisationModel =
        new OrganisationModel(
            id = id,
            name = organisation.name,
            description = organisation.description,
            email = organisation.email,
            website = organisation.website,
            location = organisation.location,
            category = organisation.category,
            subcategory = organisation.subcategory,
            image = organisation.image,
            logo = organisation.logo,
            video = organisation.video,
            verified = organisation.verified,
            verifiedAt = organisation.verifiedAt
        )

}

case class OrganisationWriteModel(
    id: Option[Long] = None,
    name: String,
    description: Option[String] = None,
    email: Option[String] = None,
    website: Option[String] = None,
    location: Option[String] = None,
    category: Option[String] = None,
    subcategory: Option[String] = None,
    image: Option[String] = None,
    logo: Option[String] = None,
    video: Option[String] = None,
    verified: Option[Boolean] = None,
    verifiedAt: Option[LocalDateTime] = None
)

object OrganisationWriteModel extends MyWriteable[OrganisationWriteModel] {
    implicit val formatter: OFormat[OrganisationWriteModel] = Json.format[OrganisationWriteModel]
}

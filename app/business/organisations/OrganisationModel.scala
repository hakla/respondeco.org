package business.organisations

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
    video: Option[String]
) extends IdModel

object OrganisationModel extends MyWriteable[OrganisationModel] {
    implicit val formatter: OFormat[OrganisationModel] = Json.format[OrganisationModel]

    def fromWriteModel(id: Long, organisation: OrganisationWriteModel): OrganisationModel =
        new OrganisationModel(id, organisation.name, organisation.description, organisation.email, organisation.website, organisation.location, organisation.category, organisation.subcategory, organisation.image, organisation.logo, organisation.video)

}

case class OrganisationWriteModel(
    id: Option[Long],
    name: String,
    description: Option[String],
    email: Option[String],
    website: Option[String],
    location: Option[String],
    category: Option[String],
    subcategory: Option[String],
    image: Option[String],
    logo: Option[String],
    video: Option[String]
)

object OrganisationWriteModel extends MyWriteable[OrganisationWriteModel] {
    implicit val formatter: OFormat[OrganisationWriteModel] = Json.format[OrganisationWriteModel]
}

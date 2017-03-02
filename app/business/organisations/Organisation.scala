package business.organisations

import common.MyWriteable
import play.api.libs.json.Json

case class Organisation(id: Long, name: String, description: Option[String], email: Option[String], website: Option[String], location: Option[String], category: Option[String], subcategory: Option[String])

object Organisation extends MyWriteable[Organisation] {
    implicit val formatter = Json.format[Organisation]
}

case class OrganisationInsert(name: String, description: Option[String], email: Option[String], website: Option[String], location: Option[String], category: Option[String], subcategory: Option[String])

object OrganisationInsert extends MyWriteable[OrganisationInsert] {
    implicit val formatter = Json.format[OrganisationInsert]
}

case class OrganisationUpdate(id: Long, name: String, description: Option[String], email: Option[String], website: Option[String], location: Option[String], category: Option[String], subcategory: Option[String])

object OrganisationUpdate extends MyWriteable[OrganisationUpdate] {
    implicit val formatter = Json.format[OrganisationUpdate]
}

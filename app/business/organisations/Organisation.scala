package business.organisations

import common.MyWriteable
import play.api.libs.json.Json

case class Organisation(id: Long, name: String)

object Organisation extends MyWriteable[Organisation] {
    implicit val formatter = Json.format[Organisation]
}

case class OrganisationInsert(name: String)

object OrganisationInsert extends MyWriteable[OrganisationInsert] {
    implicit val formatter = Json.format[OrganisationInsert]
}

case class OrganisationUpdate(id: Long, name: String)

object OrganisationUpdate extends MyWriteable[OrganisationUpdate] {
    implicit val formatter = Json.format[OrganisationUpdate]
}

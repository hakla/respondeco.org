package business.accounts

import common.{FromIdModel, IdModel, MyWriteable}
import play.api.libs.json.{Json, OFormat}
import security.Role

case class AccountModel(id: Long, email: String, name: String, password: String, role: Role, organisationId: Long) extends IdModel

case class AccountWriteModel(id: Option[Long], email: String, name: String, role: String, password: Option[String])

object AccountWriteModel extends MyWriteable[AccountWriteModel] {
    implicit val formatter: OFormat[AccountWriteModel] = Json.format[AccountWriteModel]

    def apply(account: AccountModel): AccountWriteModel = AccountWriteModel(
        id = Some(account.id),
        email = account.email,
        name = account.name,
        role = account.role.name,
        password = None
    )
}

case class RegistrationModel (email: String, name: String, password: String, organisation: String)

object RegistrationModel extends MyWriteable[RegistrationModel] {
    implicit val formatter: OFormat[RegistrationModel] = Json.format[RegistrationModel]
}

case class AccountPublicModel (id: Long, email: String, name: String, role: String, organisationId: Long) extends IdModel

object AccountPublicModel extends MyWriteable[AccountPublicModel] {

    implicit val formatter: OFormat[AccountPublicModel] = Json.format[AccountPublicModel]

    def from(a: AccountModel): AccountPublicModel = {
        AccountPublicModel(
            a.id,
            a.email,
            a.name,
            a.role.name,
            a.organisationId
        )
    }

}

case class AccountAuthenticationTry(user: String, password: String)

object AccountAuthenticationTry extends MyWriteable[AccountAuthenticationTry] {
    implicit val formatter: OFormat[AccountAuthenticationTry] = Json.format[AccountAuthenticationTry]
}

package business.accounts

import common.{FromIdModel, IdModel, MyWriteable}
import play.api.libs.json.{Json, OFormat}
import security.Role

case class AccountModel(id: Long, email: String, name: String, password: String, role: Role, organisationId: Option[Long], image: Option[String]) extends IdModel

case class AccountWriteModel(id: Option[Long], email: String, name: String, role: String, password: Option[String], organisationId: Option[Long], image: Option[String])

object AccountWriteModel extends MyWriteable[AccountWriteModel] {
    implicit val formatter: OFormat[AccountWriteModel] = Json.format[AccountWriteModel]

    def apply(account: AccountModel): AccountWriteModel = AccountWriteModel(
        id = Some(account.id),
        email = account.email,
        name = account.name,
        role = account.role.name,
        password = None,
        organisationId = account.organisationId,
        image = account.image
    )
}

case class RegistrationModel (email: String, name: String, password: String, organisation: String)

object RegistrationModel extends MyWriteable[RegistrationModel] {
    implicit val formatter: OFormat[RegistrationModel] = Json.format[RegistrationModel]
}

case class AccountPublicModel (id: Long, email: String, name: String, role: String, organisationId: Option[Long], image: Option[String]) extends IdModel

object AccountPublicModel extends MyWriteable[AccountPublicModel] {

    implicit val formatter: OFormat[AccountPublicModel] = Json.format[AccountPublicModel]

    def from(a: AccountModel): AccountPublicModel = {
        AccountPublicModel(
            id = a.id,
            email = a.email,
            name = a.name,
            role = a.role.name,
            organisationId = a.organisationId,
            image = a.image
        )
    }

}

case class AccountAuthenticationTry(user: String, password: String)

object AccountAuthenticationTry extends MyWriteable[AccountAuthenticationTry] {
    implicit val formatter: OFormat[AccountAuthenticationTry] = Json.format[AccountAuthenticationTry]
}

case class PasswordChangeRequest(oldPassword: String, newPassword: String, verifiedPassword: String)

object PasswordChangeRequest extends MyWriteable[PasswordChangeRequest] {
    implicit val formatter: OFormat[PasswordChangeRequest] = Json.format[PasswordChangeRequest]
}

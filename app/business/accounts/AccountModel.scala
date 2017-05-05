package business.accounts

import common.{FromIdModel, IdModel, MyWriteable}
import play.api.libs.json.{Json, OFormat}
import security.Role

case class AccountModel(id: Long, email: String, name: String, password: String, role: Role) extends IdModel

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

case class AccountPublicModel (id: Long, email: String, name: String, role: String) extends IdModel

object AccountPublicModel extends FromIdModel[AccountModel, AccountWriteModel] {

    def from(a: AccountModel): AccountWriteModel = {
        AccountWriteModel(a)
    }

}

case class AccountAuthenticationTry(user: String, password: String)

object AccountAuthenticationTry extends MyWriteable[AccountAuthenticationTry] {
    implicit val formatter = Json.format[AccountAuthenticationTry]
}

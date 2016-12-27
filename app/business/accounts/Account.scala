package business.accounts

import security.Role
import common.MyWriteable
import play.api.libs.json.{Json, OFormat}

/**
  * Created by Clemens Puehringer on 28/11/15.
  */
case class Account(id: Long, email: String, name: String, password: String, role: Role)

case class AccountNew(email: String, name: String, password: String)

object AccountNew extends MyWriteable[AccountNew] {
    implicit val formatter: OFormat[AccountNew] = Json.format[AccountNew]
}

case class AccountPublic(id: Long, email: String, name: String, role: String)

object AccountPublic extends MyWriteable[AccountPublic] {
    implicit val formatter: OFormat[AccountPublic] = Json.format[AccountPublic]

    def apply(account: Account): AccountPublic = AccountPublic(account.id, account.email, account.name, account.role.name)

    def from(a: Account): AccountPublic = {
        AccountPublic(a)
    }
}

case class AccountAuthenticationTry(user: String, password: String)

object AccountAuthenticationTry extends MyWriteable[AccountAuthenticationTry] {
    implicit val formatter = Json.format[AccountAuthenticationTry]
}

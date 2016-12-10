package business.accounts

import authentication.Role
import common.MyWriteable
import play.api.libs.json.{Json, OFormat}

/**
  * Created by Clemens Puehringer on 28/11/15.
  */
case class Account(id: Long, email: String, password: String, role: Role)

case class AccountNew(email: String, password: String)

object AccountNew extends MyWriteable[AccountNew] {
    implicit val formatter: OFormat[AccountNew] = Json.format[AccountNew]
}

case class AccountPublic(id: Long, email: String)

object AccountPublic extends MyWriteable[AccountPublic] {
    implicit val formatter: OFormat[AccountPublic] = Json.format[AccountPublic]

    def apply(account: Account): AccountPublic = AccountPublic(account.id, account.email)

    def from(a: Account): AccountPublic = {
        AccountPublic(a)
    }
}

package security

import anorm.{RowParser, SqlParser}

/**
  * Created by Clemens Puehringer on 06/03/16.
  */
sealed abstract case class Role(value: Int, name: String)

object Role {

    val parser : RowParser[Role] = {
        SqlParser.get[Int]("role") map Role.getRole
    }

    def getRole(value : Int) : Role = {
        value match {
            case Administrator.value => Administrator
            case User.value => User
        }
    }

    implicit def role2String(role: Role): String = role.name

}

object Administrator   extends Role(1, "Administrator")
object User            extends Role(2, "User")

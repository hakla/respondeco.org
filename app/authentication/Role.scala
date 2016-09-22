package authentication

import anorm.{RowParser, SqlParser}
import play.api.libs.json.Json

/**
  * Created by Clemens Puehringer on 06/03/16.
  */
sealed abstract case class Role(value: Int)

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
}

object Administrator   extends Role(1)
object User            extends Role(2)

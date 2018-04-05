package business.comments

import java.time.LocalDateTime

import business.accounts.AccountPublicModel
import business.organisations.OrganisationModel
import common.MyWriteable
import play.api.libs.json.{Format, JsValue, Json, OFormat}

case class CommentModel(
    id: Long,
    author: Long,
    date: Option[LocalDateTime],
    title: Option[String],
    content: Option[String],
    video: Option[String],
    image: Option[String],
    pinned: Boolean
)

object CommentModel extends MyWriteable[CommentModel] {

    implicit val formatter: Format[CommentModel] = Json.format[CommentModel]

}

case class CommentWriteModel(
    id: Option[Long],
    author: Long,
    date: Option[LocalDateTime],
    title: Option[String],
    content: Option[String],
    video: Option[String],
    image: Option[String],
    pinned: Option[Boolean]
)

object CommentWriteModel extends MyWriteable[CommentWriteModel] {

    implicit val formatter: Format[CommentWriteModel] = Json.format[CommentWriteModel]

}

case class CommentPublicModel(
    id: Long,
    date: Option[String],
    author: Option[Author],
    title: Option[String],
    content: Option[String],
    video: Option[String],
    image: Option[String],
    pinned: Option[Boolean]
)

object CommentPublicModel extends MyWriteable[CommentPublicModel] {

    implicit val formatter: Format[CommentPublicModel] = Json.format[CommentPublicModel]

}

sealed trait Author {
    def id: Long
    def image: Option[String]
    def name: String
}

object Author {

    def unapply(author: Author): Option[JsValue] = {
        val o = author match {
            case o: AuthorUser => Json.toJson(o)(AuthorUser.formatter)
            case o: AuthorOrganisation => Json.toJson(o)(AuthorOrganisation.formatter)
        }

        Some(o)
    }

    def apply(data: JsValue): Author = {
        data.validate[AuthorUser].orElse(data.validate[AuthorOrganisation]).get
    }

    implicit val formatter: OFormat[Author] = Json.format[Author]

}

case class AuthorOrganisation(
    id: Long,
    image: Option[String],
    name: String,
    organisation: OrganisationModel
) extends Author

object AuthorOrganisation extends MyWriteable[AuthorOrganisation] {

    implicit val formatter: OFormat[AuthorOrganisation] = Json.format[AuthorOrganisation]

}

case class AuthorUser(
    id: Long,
    image: Option[String],
    name: String,
    user: AccountPublicModel
) extends Author

object AuthorUser extends MyWriteable[AuthorUser] {

    implicit val formatter: OFormat[AuthorUser] = Json.format[AuthorUser]

}

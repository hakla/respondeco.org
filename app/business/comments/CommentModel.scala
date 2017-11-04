package business.comments

import business.finishedProjects.FinishedProjectPublicModel
import common.MyWriteable
import play.api.libs.json.{Format, Json}

case class CommentModel(
    id: Long,
    project_history: Long,
    author: Long,
    title: Option[String],
    content: Option[String],
    video: Option[String],
    image: Option[String]
)

object CommentModel extends MyWriteable[CommentModel] {

    implicit val formatter: Format[CommentModel] = Json.format[CommentModel]

}

case class CommentWriteModel(
    id: Option[Long],
    project_history: Long,
    author: Long,
    title: Option[String],
    content: Option[String],
    video: Option[String],
    image: Option[String]
)

object CommentWriteModel extends MyWriteable[CommentWriteModel] {

    implicit val formatter: Format[CommentWriteModel] = Json.format[CommentWriteModel]

}

case class CommentPublicModel(
    author: Option[String],
    title: Option[String],
    content: Option[String],
    video: Option[String],
    image: Option[String]
)

object CommentPublicModel extends MyWriteable[CommentPublicModel] {

    implicit val formatter: Format[CommentPublicModel] = Json.format[CommentPublicModel]

}


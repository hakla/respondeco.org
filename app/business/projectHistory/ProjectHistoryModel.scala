package business.projectHistory

import java.time.LocalDate

import business.rating.RatingWriteModel
import common.MyWriteable
import play.api.libs.json.{Format, Json}

case class ProjectHistoryModel(
    id: Long,
    project: Long,
    organisation: Long,
    rating_owner: Long,
    rating_organisation: Long,
    date: LocalDate
)

object ProjectHistoryModel extends MyWriteable[ProjectHistoryModel] {

    implicit val formatter: Format[ProjectHistoryModel] = Json.format[ProjectHistoryModel]

}

case class ProjectHistoryWriteModel(
    id: Option[Long],
    project: Long,
    organisation: Long,
    ratingOwner: RatingWriteModel,
    ratingOrganisation: RatingWriteModel,
    date: LocalDate
)

object ProjectHistoryWriteModel extends MyWriteable[ProjectHistoryWriteModel] {

    implicit val formatter: Format[ProjectHistoryWriteModel] = Json.format[ProjectHistoryWriteModel]

}

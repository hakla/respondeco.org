package business.finishedProjects

import java.time.LocalDate

import business.organisations.OrganisationModel
import business.projects.ProjectModel
import business.rating.{RatingModel, RatingWriteModel}
import common.MyWriteable
import play.api.libs.json.{Format, Json}

case class FinishedProjectModel(
    id: Long,
    project: Long,
    organisation: Long,
    rating_owner: Long,
    rating_organisation: Long,
    date: LocalDate
)

object FinishedProjectModel extends MyWriteable[FinishedProjectModel] {

    implicit val formatter: Format[FinishedProjectModel] = Json.format[FinishedProjectModel]

}

case class FinishedProjectWriteModel(
    id: Option[Long],
    project: Long,
    organisation: Long,
    ratingOwner: RatingWriteModel,
    ratingOrganisation: RatingWriteModel,
    date: LocalDate
)

object FinishedProjectWriteModel extends MyWriteable[FinishedProjectWriteModel] {

    implicit val formatter: Format[FinishedProjectWriteModel] = Json.format[FinishedProjectWriteModel]

}

case class FinishedProjectPublicModel(
    id: Long,
    project: Option[ProjectModel],
    organisation: Option[OrganisationModel],
    ratingOwner: Option[RatingModel],
    ratingOrganisation: Option[RatingModel],
    date: LocalDate
)

object FinishedProjectPublicModel extends MyWriteable[FinishedProjectPublicModel] {

    implicit val formatter: Format[FinishedProjectPublicModel] = Json.format[FinishedProjectPublicModel]

}

package business.projects

import java.time.LocalDate

import common.MyWriteable
import persistence.Queries
import play.api.libs.json.{Format, Json}

case class ProjectModel (id: Long, name: Option[String], location: Option[String], description: Option[String], category: Option[String], subcategory: Option[String], start: Option[LocalDate], end: Option[LocalDate], benefits: Option[String], price: Option[Long], organisation: Option[Long])

object ProjectModel extends MyWriteable[ProjectModel] {
    implicit val formatter: Format[ProjectModel] = Json.format

    def fromWriteModel(id: Long, project: ProjectWriteModel): ProjectModel = ProjectModel(
        id = id,
        name = project.name,
        location = project.location,
        description = project.description,
        category = project.category,
        subcategory = project.subcategory,
        start = project.start,
        end = project.end,
        benefits = project.benefits,
        price = project.price,
        organisation = project.organisation
    )
}

case class ProjectWriteModel (id: Option[Long], name: Option[String], location: Option[String], description: Option[String], category: Option[String], subcategory: Option[String], start: Option[LocalDate], end: Option[LocalDate], benefits: Option[String], price: Option[Long], organisation: Option[Long])

object ProjectWriteModel extends MyWriteable[ProjectWriteModel] {
    implicit val formatter: Format[ProjectWriteModel] = Json.format
}

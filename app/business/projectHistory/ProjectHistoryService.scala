package business.projectHistory

import javax.inject.Inject

import anorm.{Macro, RowParser, SQL}
import business.rating.{RatingService, RatingWriteModel}
import common.Database
import persistence.Queries

/**
  * Created by klaus on 01.06.17.
  */
class ProjectHistoryService @Inject()(ratingService: RatingService, implicit val db: Database) extends Queries[ProjectHistoryModel] {

    implicit val parser: RowParser[ProjectHistoryModel] = Macro.namedParser[ProjectHistoryModel]
    implicit val table: String = "project_history"

    def create(projectHistoryEntry: ProjectHistoryWriteModel): Option[ProjectHistoryWriteModel] = db.withConnection { implicit c =>
        val ownerRating = ratingService.create(projectHistoryEntry.ratingOwner).get.id

        val organisationRating = ratingService.create(projectHistoryEntry.ratingOrganisation).get.id

        SQL(s"insert into $table (id, project, organisation, rating_owner, rating_organisation, `date`) values(null, {project}, {organisation}, {ratingOwner}, {ratingOrganisation}, {date})").on(
            'project -> projectHistoryEntry.project,
            'organisation -> projectHistoryEntry.organisation,
            'ratingOwner -> ownerRating,
            'ratingOrganisation -> organisationRating,
            'date -> projectHistoryEntry.date.atStartOfDay
        ).executeInsert().asInstanceOf[Option[Long]].flatMap(byIdAsWriteModel)
    }

    def update(id: Long, projectHistoryEntry: ProjectHistoryWriteModel): Option[ProjectHistoryWriteModel] = db.withConnection { implicit c =>
        SQL(s"update $table set id = {id}, project = {project}, organisation = {organisation}, date = {date} where id = {id}").on(
            'project -> projectHistoryEntry.project,
            'organisation -> projectHistoryEntry.organisation,
            'date -> projectHistoryEntry.date.atStartOfDay
        ).executeUpdate() match {
            case 1 => byIdAsWriteModel(id)
            case _ => None
        }
    }

    def toWriteModel(historyEntry: ProjectHistoryModel) = {
        ProjectHistoryWriteModel(
            id = Some(historyEntry.id),
            project = historyEntry.project,
            organisation = historyEntry.organisation,
            ratingOwner = ratingService.byId(historyEntry.rating_owner).map(_.toWriteModel).getOrElse(RatingWriteModel()),
            ratingOrganisation = ratingService.byId(historyEntry.rating_organisation).map(_.toWriteModel).getOrElse(RatingWriteModel()),
            date = historyEntry.date
        )
    }

    def findByProject(projectId: Long): List[ProjectHistoryWriteModel] = {
        all(
            'project -> projectId
        ).map(toWriteModel)
    }

    def byIdAsWriteModel(id: Long): Option[ProjectHistoryWriteModel] = byId(id).map(toWriteModel)

}

package business.finishedProjects

import javax.inject.Inject

import anorm.{Macro, RowParser, SQL}
import business.comments.CommentService
import business.organisations.OrganisationService
import business.projects.{ProjectModel, ProjectPublicModel, ProjectService}
import business.rating.{RatingPublicModel, RatingService, RatingWriteModel}
import common.Database
import persistence.Queries

class FinishedProjectService @Inject()(commentService: CommentService, organisationService: OrganisationService, projectService: ProjectService, ratingService: RatingService, implicit val db: Database) extends Queries[FinishedProjectModel] {

    implicit val parser: RowParser[FinishedProjectModel] = Macro.namedParser[FinishedProjectModel]
    implicit val table: String = "project_history"

    def create(finishedProject: FinishedProjectWriteModel): Option[FinishedProjectWriteModel] = db.withConnection { implicit c =>
        val ownerRating = ratingService.create(finishedProject.ratingOwner).get.id

        val organisationRating = ratingService.create(finishedProject.ratingOrganisation).get.id

        SQL(s"insert into $table (id, project, organisation, rating_owner, rating_organisation, `date`) values(null, {project}, {organisation}, {ratingOwner}, {ratingOrganisation}, {date})").on(
            'project -> finishedProject.project,
            'organisation -> finishedProject.organisation,
            'ratingOwner -> ownerRating,
            'ratingOrganisation -> organisationRating,
            'date -> finishedProject.date.atStartOfDay
        ).executeInsert().asInstanceOf[Option[Long]].flatMap(byIdAsWriteModel)
    }

    def update(id: Long, finishedProject: FinishedProjectWriteModel): Option[FinishedProjectWriteModel] = db.withConnection { implicit c =>

        ratingService.update(finishedProject.ratingOwner)
        ratingService.update(finishedProject.ratingOrganisation)

        SQL(s"update $table set id = {id}, project = {project}, organisation = {organisation}, date = {date} where id = {id}").on(
            'id -> id,
            'project -> finishedProject.project,
            'organisation -> finishedProject.organisation,
            'date -> finishedProject.date.atStartOfDay
        ).executeUpdate() match {
            case 1 => byIdAsWriteModel(id)
            case _ => None
        }

    }

    def toWriteModel(historyEntry: FinishedProjectModel): FinishedProjectWriteModel = {
        FinishedProjectWriteModel(
            id = Some(historyEntry.id),
            project = historyEntry.project,
            organisation = historyEntry.organisation,
            ratingOwner = ratingService.byId(historyEntry.rating_owner).map(_.toWriteModel).getOrElse(RatingWriteModel()),
            ratingOrganisation = ratingService.byId(historyEntry.rating_organisation).map(_.toWriteModel).getOrElse(RatingWriteModel()),
            date = historyEntry.date
        )
    }

    def toPublicModel(finishedProject: FinishedProjectModel): FinishedProjectPublicModel = {
        FinishedProjectPublicModel(
            id = finishedProject.id,
            project = projectService.byId(finishedProject.project).map(projectService.toPublicModel),
            organisation = organisationService.byId(finishedProject.organisation),
            ratingOwner = ratingService.byId(finishedProject.rating_owner).map(_.toPublicModel),
            ratingOrganisation = ratingService.byId(finishedProject.rating_organisation).map(_.toPublicModel),
            date = finishedProject.date,
            comments = commentService.all('project_history -> finishedProject.id).size
        )
    }

    def findByOrganisation(organisationId: Long): List[FinishedProjectPublicModel] = {
        all(
            'organisation -> organisationId
        ).map(toPublicModel)
    }

    def findByProject(projectId: Long): List[FinishedProjectPublicModel] = {
        all(
            'project -> projectId
        ).map(toPublicModel)
    }

    def byIdAsWriteModel(id: Long): Option[FinishedProjectWriteModel] = byId(id).map(toWriteModel)

}

package business.comments

import javax.inject.Inject

import anorm.{Macro, RowParser, SQL}
import business.finishedProjects.FinishedProjectWriteModel
import business.organisations.{OrganisationModel, OrganisationService, OrganisationWriteModel}
import common.Database
import persistence.Queries
import se.digiplant.res.api.Res

class CommentService @Inject()(implicit val db: Database, val res: Res, val organisationService: OrganisationService) extends Queries[CommentModel] {

    implicit val parser: RowParser[CommentModel] = Macro.namedParser[CommentModel]
    implicit val table: String = "comment"

    def create(comment: CommentWriteModel) : Option[CommentModel] = db.withConnection { implicit c =>
        SQL(s"insert into $table (project_history, author, title, content, image, video) values({project_history}, {author}, {title}, {content}, {image}, {video})").on(
            'project_history -> comment.project_history,
            'author -> comment.author,
            'title -> comment.title,
            'content -> comment.content,
            'image -> comment.image,
            'video -> comment.video
        ).executeInsert().asInstanceOf[Option[Long]].map { byId } getOrElse None
    }

    def update(id: Long, comment: CommentWriteModel): Option[CommentModel] = db.withConnection { implicit c =>
        // remove old image (if there was one)
        byId(id).flatMap { _.image } map { image => res.delete(image) }

        SQL(s"update $table set project_history = {project_history}, author = {author}, title = {title}, content = {content}, image = {image}, video = {video} where id = {id}").on(
            'id -> id,
            'project_history -> comment.project_history,
            'author -> comment.author,
            'title -> comment.title,
            'content -> comment.content,
            'image -> comment.image,
            'video -> comment.video
        ).executeUpdate() match {
            case 1 => byId(id)
            case _ => None
        }
    }

    def toPublicModel(commentModel: CommentModel) : CommentPublicModel = CommentPublicModel(
        author = organisationService.byId(commentModel.author).map(_.name),
        title = commentModel.title,
        content = commentModel.content,
        video = commentModel.video,
        image = commentModel.image
    )

}

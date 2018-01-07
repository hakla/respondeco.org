package business.comments

import java.time.LocalDateTime
import javax.inject.Inject

import anorm.{Macro, NamedParameter, RowParser, SQL}
import business.accounts.{AccountPublicModel, AccountService}
import business.organisations.OrganisationService
import common.Database
import persistence.Queries
import se.digiplant.res.api.Res

class CommentService @Inject()(implicit val db: Database, val res: Res, val accountService: AccountService, val organisationService: OrganisationService) extends Queries[CommentModel] {

    import common.DateImplicits._

    implicit val parser: RowParser[CommentModel] = Macro.namedParser[CommentModel]
    implicit val table: String = "comment"

    override def all: List[CommentModel] = super.all(
        'status -> false
    )

    override def all(namedParameter: NamedParameter*): List[CommentModel] = db.withConnection { implicit connection =>
        where(
            namedParameter :+ NamedParameter.symbol('status -> false): _*
        ).executeQuery().as(parser.*)
    }

    def create(comment: CommentWriteModel, commentType: String, linkId: Long): Option[CommentModel] = db.withConnection { implicit c =>
        val newComment: Option[CommentModel] = SQL(s"insert into $table (author, date, title, content, image, video) values({author}, {date}, {title}, {content}, {image}, {video})").on(
            'author -> comment.author,
            'title -> comment.title,
            'date -> comment.date.flatMap(_.formatted).orElse(LocalDateTime.now().formatted),
            'content -> comment.content,
            'image -> comment.image,
            'video -> comment.video,
            'updatedAt -> LocalDateTime.now().formatted
        ).executeInsert() match {
            case Some(id: Long) => byId(id)
            case _ => None
        }

        newComment.map(c => {
            commentType match {
                case "project" => createProjectLink(c, linkId)
                case "finishedProject" => createFinishedProjectLink(c, linkId)
            }
        })

        newComment
    }

    def update(id: Long, comment: CommentWriteModel): Option[CommentModel] = db.withConnection { implicit c =>
        // remove old image (if there was one)
        byId(id).flatMap {
            _.image
        } map { image => res.delete(image) }

        SQL(s"update $table set author = {author}, date = {date}, title = {title}, content = {content}, image = {image}, video = {video}, updatedAt = CURRENT_TIMESTAMP where id = {id}").on(
            'id -> id,
            'author -> comment.author,
            'date -> comment.date.flatMap(_.formatted).orElse(LocalDateTime.now().formatted),
            'title -> comment.title,
            'content -> comment.content,
            'image -> comment.image,
            'video -> comment.video
        ).executeUpdate() match {
            case 1 => byId(id)
            case _ => None
        }
    }

    def authorFromUser(comment: CommentModel): Option[Author] = {
        accountService
            .byId(comment.author)
            .map(author => AuthorUser(
                author.id,
                author.image,
                author.name,
                AccountPublicModel.from(author)
            ))
    }

    def authorFromOrganisation(comment: CommentModel): Option[Author] = {
        organisationService.byId(comment.author)
            .map(author => AuthorOrganisation(
                author.id,
                author.logo,
                author.name,
                author
            ))
    }

    def authorFromComment(comment: CommentModel): Option[Author] = {
        authorFromUser(comment).orElse(authorFromOrganisation(comment))
    }

    def toPublicModel(commentModel: CommentModel): CommentPublicModel = {
        CommentPublicModel(
            id = commentModel.id,
            author = authorFromComment(commentModel),
            date = commentModel.date.formatted,
            title = commentModel.title,
            content = commentModel.content,
            video = commentModel.video,
            image = commentModel.image
        )
    }

    override def delete(id: Long): Boolean = db.withConnection { implicit c =>
        SQL(s"update $table set status = false, updatedAt = CURRENT_TIMESTAMP where id = {id}").on(
            'id -> id
        ).executeUpdate() == 1
    }

    def byProject(id: Long): List[CommentModel] = db.withConnection { implicit c =>
        SQL(s"select c.* from comment c join comment_project cp on c.id = cp.comment where cp.project = $id and status = true order by c.date desc")
            .executeQuery()
            .as(parser.*)
    }

    def byFinishedProject(id: Long): List[CommentModel] = db.withConnection { implicit c =>
        SQL(s"select c.* from comment c join comment_project_history cph on c.id = cph.comment where cph.project_history = $id and status = true order by c.date desc")
            .executeQuery()
            .as(parser.*)
    }

    def createProjectLink(comment: CommentModel, projectId: Long): CommentModel = db.withConnection { implicit c =>
        SQL(s"insert into comment_project (comment, project) values ({comment}, {project})")
            .on(
                'comment -> comment.id,
                'project -> projectId
            ).executeInsert()

        comment
    }

    def createFinishedProjectLink(comment: CommentModel, projectId: Long): CommentModel = db.withConnection { implicit c =>
        SQL(s"insert into comment_project_history (comment, project_history) values ({comment}, {project_history})")
            .on(
                'comment -> comment.id,
                'project_history -> projectId
            ).executeInsert()

        comment
    }

}

package business.comments

import javax.inject.Inject

import business.accounts.AccountService
import common.Pagination
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}
import security.{AuthenticatedController, Authorization}

class CommentCtrl @Inject()(commentService: CommentService, val accountService: AccountService) extends AuthenticatedController with Authorization with Pagination {

    def create(commentType: String, linkId: Long): Action[CommentWriteModel] = Unauthenticated(parse.json[CommentWriteModel]) { (comment, request) =>
        commentService.create(comment, commentType, linkId) match {
            case Some(newComment) => Ok(newComment)
            case None => BadRequest("Could not create")
        }
    }

    def findAll(): Action[AnyContent] = Unauthenticated { request =>
        Ok(
            Json.toJson(
                paginated(request, commentService.all.map(commentService.toPublicModel))
            )
        )
    }

    def byId(id: Long): Action[AnyContent] = Unauthenticated {
        Ok(
            Json.toJson(commentService.byId(id).map(commentService.toPublicModel))
        )
    }

    def update(id: Long): Action[CommentWriteModel] = AuthenticatedUser(parse.json[CommentWriteModel]) { (comment, _) =>
        commentService.update(id, comment) match {
            case Some(updatedComment) => Ok(updatedComment)
            case None => BadRequest("Could not update")
        }
    }

    def delete(id: Long) = AuthenticatedUser {
        if (commentService.delete(id)) {
            Ok
        } else {
            BadRequest
        }
    }

    def findByProject(id: Long): Action[AnyContent] = Unauthenticated {
        Ok(
            commentService.byProject(id).map(commentService.toPublicModel)
        )
    }

    def findByFinishedProject(id: Long): Action[AnyContent] = Unauthenticated {
        Ok(
            commentService.byFinishedProject(id).map(commentService.toPublicModel)
        )
    }

    def pin (id: Long) = AuthenticatedUser(parse.empty) { (_, request) =>
        if (commentService.pin(id)) {
            Ok
        } else {
            BadRequest
        }
    }

    def unpin (id: Long) = AuthenticatedUser(parse.empty) { (_, request) =>
        if (commentService.unpin(id)) {
            Ok
        } else {
            BadRequest
        }
    }
}

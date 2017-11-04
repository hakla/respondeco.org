package business.comments

import javax.inject.Inject

import business.accounts.AccountService
import play.api.mvc.{Action, AnyContent}
import security.{AuthenticatedController, Authorization}

class CommentCtrl @Inject()(commentService: CommentService, val accountService: AccountService) extends AuthenticatedController with Authorization {

    def create(): Action[CommentWriteModel] = AuthenticatedUser(parse.json[CommentWriteModel]) { (comment, request) =>
        commentService.create(comment) match {
            case Some(newComment) => Ok(newComment)
            case None => BadRequest("Could not create")
        }
    }

    def findAll(): Action[AnyContent] = Unauthenticated {
        Ok(commentService.all.map(commentService.toPublicModel))
    }

    def findByProject(id: Long): Action[AnyContent] = Unauthenticated {
        Ok(
            commentService.all(
                'project_history -> id
            ).map(commentService.toPublicModel)
        )
    }
}

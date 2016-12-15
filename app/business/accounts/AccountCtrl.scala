package business.accounts

import javax.inject.Inject

import authentication.{AuthenticatedController, User}
import jp.t2v.lab.play2.stackc.{Attribute, RequestWithAttributes}
import play.api.libs.json.Json
import play.api.mvc.{Action, Result}

/**
  * Created by Clemens Puehringer on 28/11/15.
  */
class AccountCtrl @Inject()(val accountService: AccountService) extends AuthenticatedController {

    def register = Action(parse.json) { request =>
        Ok
    }

    def findAll = StackAction(AuthorityKey -> User) { implicit request =>
        Ok(accountService.all().map(AccountPublic.from))
    }

    def create = Action(parse.json[AccountNew]) { request =>
        accountService.create(request.body) match {
            case Some(account) => Ok(AccountPublic from account)
            case None => BadRequest("Couldn't create")
        }
    }

    def findById(id: Long) = StackAction(AuthorityKey -> User) { implicit request =>
        accountService.byId(id) match {
            case Some(account) => Ok(Json.toJson(AccountPublic from account))
            case None => BadRequest("No such Account")
        }
    }


    def update(id: Long) = StackAction[AccountPublic](parse.json[AccountPublic], AuthorityKey -> User) { implicit request =>
        accountService.update(id, request.body) match {
            case Some(account) => Ok(AccountPublic from account)
            case None => BadRequest("Couldn't update")
        }
    }

    def currentUser() = StackAction(AuthorityKey -> User) { implicit request =>
        Ok(AccountPublic from loggedIn)
    }
}

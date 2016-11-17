package business.accounts

import javax.inject.Inject

import authentication.AuthenticatedController
import play.api.libs.json.Json
import play.api.mvc.{Action, RequestHeader}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Clemens Puehringer on 28/11/15.
  */
class AccountCtrl @Inject()(val accountService: AccountService) extends AuthenticatedController {

    def login = Action.async(parse.json[AccountNew]) { implicit request =>
        gotoLoginSucceeded(request.body.email)
    }

    def logout = Action.async(parse.json) { implicit request: RequestHeader =>
        gotoLogoutSucceeded
    }

    def register = Action(parse.json) { request =>
        Ok
    }

    def findAll = Action {
        Ok(accountService.findAll().map(AccountPublic.from))
    }

    def create = Action(parse.json[AccountNew]) { request =>
        accountService.create(request.body) match {
            case Some(account) => Ok(AccountPublic from account)
            case None => BadRequest("Couldn't create")
        }
    }

    def findById(id: Long) = Action {
        accountService.findById(id) match {
            case Some(account) => Ok(Json.toJson(AccountPublic from account))
            case None => BadRequest("No such Account")
        }
    }

    def update(id: Long) = Action(parse.json[AccountPublic]) { request =>
        accountService.update(id, request.body) match {
            case Some(account) => Ok(AccountPublic from account)
            case None => BadRequest("Couldn't update")
        }
    }
}

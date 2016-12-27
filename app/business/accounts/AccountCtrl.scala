package business.accounts

import javax.inject.Inject

import security.{Administrator, AuthenticatedController, Authorization, User}
import jp.t2v.lab.play2.stackc.{Attribute, RequestWithAttributes}
import play.api.libs.json.Json
import play.api.mvc.{Action, Result}

/**
  * Created by Clemens Puehringer on 28/11/15.
  */
class AccountCtrl @Inject()(val accountService: AccountService) extends AuthenticatedController with Authorization {

    def register = Action(parse.json) { request =>
        Ok
    }

    def findAll = Unauthenticated {
        Ok(accountService.all().map(AccountPublic.from))
    }

    def create = AuthenticatedAdmin(parse.json[AccountNew]) { account =>
        accountService.create(account) match {
            case Some(account) => Ok(AccountPublic from account)
            case None => BadRequest("Couldn't create")
        }
    }

    def findById(id: Long) = AuthenticatedAdmin {
        accountService.byId(id) match {
            case Some(account) => Ok(Json.toJson(AccountPublic from account))
            case None => BadRequest("No such Account")
        }
    }


    def update(id: Long) = AuthenticatedUser(parse.json[AccountPublic]) { account =>
        accountService.update(id, account) match {
            case Some(account) => Ok(AccountPublic from account)
            case None => BadRequest("Couldn't update")
        }
    }

    def currentUser() = AuthenticatedUser { implicit request =>
        Ok(AccountPublic from loggedIn)
    }
}

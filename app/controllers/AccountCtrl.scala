package controllers

import models.{Account, Organization, AccountCreate}
import play.api.libs.json.{Json, JsError}
import play.api.mvc.{Action, Controller}
import services.AccountService

import scala.util.{Success, Failure}

/**
  * Created by Clemens Puehringer on 28/11/15.
  */
class AccountCtrl extends Controller {

    val accountService = new AccountService()

    def create = Action(parse.json) {
        request =>
            request.body.validate[AccountCreate].fold(
                errors => BadRequest(JsError.toFlatJson(errors)),
                account => {
                    accountService.create(account) match {
                        case Success(account) => Ok(Json.toJson(account))
                        case Failure(ex) => BadRequest(ex.getMessage)
                    }
                }
            )
    }

    def findById(id: Long) = Action {
        Account.findById(id) match {
            case Some(acc) => Ok(Json.toJson(acc))
            case None => BadRequest("No such Account")
        }
    }

}

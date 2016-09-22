package controllers

import authentication.Play2AuthConfig
import jp.t2v.lab.play2.auth.LoginLogout
import models.{AccountPublic, AccountNew, Account}
import play.api.libs.json.{Json, JsError}
import play.api.mvc.{Action, Controller}
import play.api.mvc.RequestHeader
import services.AccountService

import scala.concurrent.{ExecutionContext, Await}
import scala.concurrent.duration.DurationInt
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}

/**
  * Created by Clemens Puehringer on 28/11/15.
  */
class AccountCtrl extends Controller with LoginLogout with Play2AuthConfig {

    val accountService = new AccountService()

    def login = Action.async(parse.json) { implicit request =>
        val email = (request.body    \ "email").as[String]
        val password = (request.body \ "password").as[String]
        gotoLoginSucceeded(email)
    }

    def logout = Action.async(parse.json) { implicit request : RequestHeader =>
        gotoLogoutSucceeded
    }

    def register = Action(parse.json) { request =>
        Ok
    }

    def findAll = Action {
        Ok(Json.toJson(
            accountService.findAll().map(AccountPublic.from)
        ))
    }

    def create = Action(parse.json) {
        request =>
            request.body.validate[AccountNew].fold(
                errors => BadRequest(JsError.toJson(errors)),
                account => {
                    accountService.create(account) match {
                        case Success(_account) => Ok(Json.toJson(AccountPublic from _account))
                        case Failure(ex) => BadRequest(ex.getMessage)
                    }
                }
            )
    }

    def findById(id: Long) = Action {
        Account.findById(id) match {
            case Some(acc) => Ok(Json.toJson(AccountPublic from acc))
            case None => BadRequest("No such Account")
        }
    }

    def update(id: Long) = Action(parse.json) { request =>
        request.body.validate[AccountPublic].fold(
            errors => BadRequest(JsError.toJson(errors)),
            account => {
                accountService.update(id, account) match {
                    case Success(_account) => Ok(Json.toJson(AccountPublic from _account))
                    case Failure(ex) => BadRequest(ex.getMessage)
                }
            }
        )
    }
}

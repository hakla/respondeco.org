package business.accounts

import javax.inject.Inject

import common.CrudService
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Action
import security.{AuthenticatedController, Authorization}

/**
  * Created by Clemens Puehringer on 28/11/15.
  */
class AccountCtrl @Inject()(val accountService: AccountService) extends AuthenticatedController with Authorization {

    val service: AccountService = accountService

    def findAll = Unauthenticated {
        Ok(service.all().map(AccountPublicModel.from))
    }

    def findById(id: Long) = AuthenticatedAdmin {
        service.byId(id) match {
            case Some(obj) => Ok(Json.toJson(AccountPublicModel.from(obj)))
            case None => BadRequest("No such Account")
        }
    }

    def create: Action[RegistrationModel] = AuthenticatedAdmin(parse.json[RegistrationModel]) { obj =>
        service.create(obj) match {
            case Some(createdObj) => Ok(AccountPublicModel.from(createdObj))
            case None => BadRequest("Couldn't create")
        }
    }

    def update(id: Long) = AuthenticatedUser(parse.json[AccountWriteModel]) { (obj, _) =>
        service.update(id, obj) match {
            case Some(updatedObj) => Ok(AccountPublicModel.from(updatedObj))
            case None => BadRequest("Couldn't update")
        }
    }

    def register: Action[JsValue] = Action(parse.json) { request =>
        Ok
    }

    def currentUser() = AuthenticatedUser { implicit request =>
        Ok(AccountPublicModel from loggedIn)
    }
}

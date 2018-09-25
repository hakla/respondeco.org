package business.accounts

import business.organisations.OrganisationExists
import javax.inject.Inject
import common.Pagination
import play.api.libs.json.Json
import play.api.mvc.Action
import security.{AuthenticatedController, Authorization}

import scala.util.Success

/**
  * Created by Clemens Puehringer on 28/11/15.
  */
class AccountCtrl @Inject()(val accountService: AccountService) extends AuthenticatedController with Authorization with Pagination {

    val service: AccountService = accountService

    def delete(id: Long) = AuthenticatedAdmin {
        service.delete(id) match {
            case true => Ok
            case false => BadRequest
        }
    }

    def findAll = AuthenticatedAdmin { request =>
        Ok(
            Json.toJson(
                paginated(request, service.all().map(AccountPublicModel.from))
            )
        )
    }

    def findById(id: Long) = AuthenticatedAdmin {
        service.byId(id) match {
            case Some(obj) => Ok(AccountPublicModel.from(obj))
            case None => BadRequest("No such Account")
        }
    }

    def create: Action[AccountWriteModel] = AuthenticatedAdmin(parse.json[AccountWriteModel]) { account =>
        service.createByAdmin(account) match {
            case Some(createdObj) => Ok(AccountPublicModel.from(createdObj))
            case None => BadRequest("Couldn't create")
        }
    }

    def register: Action[RegistrationModel] = Unauthenticated(parse.json[RegistrationModel]) { (registration, request) =>
        try {
            val user = service.createFromRegistration(registration)

            Ok(AccountPublicModel.from(user))
        } catch {
            case e: AccountCreationFailed => BadRequest("user.creation.failed")
            case e: AccountWithEmailExists => BadRequest("user.already.registered.with.email")
            case e: AccountWithNameExists => BadRequest("user.already.registered.with.name")
            case e: OrganisationExists => BadRequest("organisation.exists")
        }
    }

    def changePassword(): Action[PasswordChangeRequest] = AuthenticatedUser(parse.json[PasswordChangeRequest]) { (changeRequest, request) =>
        val user = loggedIn(request)

        if (changeRequest.newPassword != changeRequest.verifiedPassword) BadRequest("New password doesn't match verification!")
        else service.changePassword(user.id, changeRequest) match {
            case true => Ok
            case false => Forbidden
        }
    }

    def update(id: Long) = AuthenticatedUser(parse.json[AccountWriteModel]) { (obj, request) =>
        assertUser(id == _.id, request) {
            service.updateByUser(id, obj) match {
                case Some(updatedObj) => Ok(AccountPublicModel.from(updatedObj))
                case None => BadRequest("Couldn't update")
            }
        }
    }

    def updateAdmin(id: Long) = AuthenticatedAdmin(parse.json[AccountWriteModel]) { (obj) =>
        service.update(id, obj) match {
            case Some(updatedObj) => Ok(AccountPublicModel.from(updatedObj))
            case None => BadRequest("Couldn't update")
        }
    }

    def currentUser() = AuthenticatedUser { implicit request =>
        Ok(AccountPublicModel from loggedIn)
    }
}

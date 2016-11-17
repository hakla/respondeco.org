package business.organisations

import javax.inject.Inject

import authentication.AuthenticatedController
import business.accounts.AccountService
import play.api.mvc._

class OrganisationCtrl @Inject()(organisationService: OrganisationService, val accountService: AccountService) extends AuthenticatedController {

    def findAll = Action {
        Ok(organisationService.findAll())
    }

    def findById(id: Long) = Action {
        organisationService.findById(id) match {
            case Some(org) => Ok(org)
            case None => BadRequest("No such Organization")
        }
    }

    def update(id: Long) = Action(parse.json[OrganisationInsert]) { request =>
        organisationService.update(id, request.body) match {
            case Some(org) => Ok(org)
            case None => BadRequest("Could not update")
        }
    }

    def create = Action(parse.json[OrganisationInsert]) { request =>
        organisationService.create(request.body) match {
            case Some(org) => Ok(org)
            case None => BadRequest("Could not create")
        }
    }

}

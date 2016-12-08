package business.organisations

import javax.inject.Inject

import authentication.{AuthenticatedController, User}
import business.accounts.{AccountNew, AccountService}
import play.api.mvc._

class OrganisationCtrl @Inject()(organisationService: OrganisationService, val accountService: AccountService) extends AuthenticatedController {

    def findAll = StackAction(AuthorityKey -> User) { implicit request =>
        Ok(organisationService.all())
    }

    def findById(id: Long) = Action {
        organisationService.byId(id) match {
            case Some(org) => Ok(org)
            case None => NotFound("No such organisation")
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

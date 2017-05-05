package business.organisations

import javax.inject.Inject

import business.accounts.AccountService
import security.{AuthenticatedController, Authorization}

class OrganisationCtrl @Inject()(organisationService: OrganisationService, val accountService: AccountService) extends AuthenticatedController with Authorization {

    def findAll = Unauthenticated {
        Ok(organisationService.all())
    }

    def findById(id: Long) = Unauthenticated {
        organisationService.byId(id) match {
            case Some(org) => Ok(org)
            case None => NotFound("No such organisation")
        }
    }

    def update(id: Long) = AuthenticatedUser(parse.json[OrganisationWriteModel]) { organisation =>
        organisationService.update(id, organisation) match {
            case Some(org) => Ok(org)
            case None => BadRequest("Could not update")
        }
    }

    def create = AuthenticatedUser(parse.json[OrganisationWriteModel]) { organisation =>
        organisationService.create(organisation) match {
            case Some(org) => Ok(org)
            case None => BadRequest("Could not create")
        }
    }

    def delete(id: Long) = AuthenticatedUser {
        if (organisationService.delete(id)) {
            Ok
        } else {
            BadRequest
        }
    }

}

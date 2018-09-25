package business.organisations

import javax.inject.Inject

import business.accounts.AccountService
import common.Pagination
import play.api.libs.json._
import se.digiplant.res.api.Res
import security.{AuthenticatedController, Authorization}

class OrganisationCtrl @Inject()(organisationService: OrganisationService, val accountService: AccountService, val res: Res) extends AuthenticatedController with Authorization with Pagination {

    def findAll = Unauthenticated { request =>
        Ok(
            Json.toJson(paginated(request, organisationService.all))
        )
    }

    def findById(id: Long) = Unauthenticated {
        organisationService.byId(id) match {
            case Some(org) => Ok(org)
            case None => NotFound("No such organisation")
        }
    }

    def update(id: Long) = AuthenticatedUser(parse.json[OrganisationWriteModel]) { (organisation, request) =>
        assertUser(_.organisationId.contains(id), request) {
            try {
                organisationService.update(id, organisation) match {
                    case Some(org) => Ok(org)
                    case None => BadRequest("Could not update")
                }
            } catch {
                case e: OrganisationExists => BadRequest("organisation.exists")
            }
        }
    }

    def create = AuthenticatedUser(parse.json[OrganisationWriteModel]) { (organisation, _) =>
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

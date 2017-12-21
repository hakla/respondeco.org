package business.projects

import javax.inject.Inject

import business.accounts.AccountService
import security.{AuthenticatedController, Authorization}

class ProjectCtrl @Inject()(projectService: ProjectService, val accountService: AccountService) extends AuthenticatedController with Authorization {

    def findAll = Unauthenticated {
        Ok(projectService.all())
    }

    def findById(id: Long) = Unauthenticated {
        projectService.byId(id) match {
            case Some(org) => Ok(org)
            case None => NotFound("No such organisation")
        }
    }

    def update(id: Long) = AuthenticatedUser(parse.json[ProjectWriteModel]) { (project, _) =>
        projectService.update(id, project) match {
            case Some(org) => Ok(org)
            case None => BadRequest("Could not update")
        }
    }

    def create = AuthenticatedUser(parse.json[ProjectWriteModel]) { (project, _) =>
        projectService.create(project) match {
            case Some(org) => Ok(org)
            case None => BadRequest("Could not create")
        }
    }

    def delete(id: Long) = AuthenticatedUser {
        if (projectService.delete(id)) {
            Ok
        } else {
            BadRequest
        }
    }

    def byOrganisationId(id: Long) = Unauthenticated {
        Ok(projectService.findByOrganisation(id))
    }

}

package business.finishedProjects

import javax.inject.Inject

import business.accounts.AccountService
import business.projects.ProjectService
import play.api.mvc.Action
import security.{AuthenticatedController, Authorization}

class FinishedProjectCtrl @Inject()(finishedProjectService: FinishedProjectService, val accountService: AccountService) extends AuthenticatedController with Authorization {

    def findAll = Unauthenticated {
        Ok(
            finishedProjectService
                .all
                .map(finishedProjectService.toPublicModel)
        )
    }

    def findById(id: Long, public: Option[String] = None) = Unauthenticated {
        finishedProjectService.byId(id) match {
            case Some(finishedProject) => public match {
                case Some(_) => Ok(finishedProjectService.toPublicModel(finishedProject))
                case _ => Ok(finishedProjectService.toWriteModel(finishedProject))
            }
            case None => NotFound("No such project history entry")
        }
    }

    def create: Action[FinishedProjectWriteModel] = AuthenticatedUser(parse.json[FinishedProjectWriteModel]) { (finishedProject, _) =>
        finishedProjectService.create(finishedProject) match {
            case Some(newProjectHistoryEntry) => Ok(newProjectHistoryEntry)
            case None => BadRequest("Could not create")
        }
    }

    def update(id: Long): Action[FinishedProjectWriteModel] = AuthenticatedUser(parse.json[FinishedProjectWriteModel]) { (finishedProject, _) =>
      finishedProjectService.update(id, finishedProject) match {
          case Some(updatedProject) => Ok(updatedProject)
          case None => BadRequest("Could not update")
      }
    }

    def findByOrganisation(organisationId: Long) = Unauthenticated {
        Ok(finishedProjectService.findByOrganisation(organisationId))
    }

    def findByProject(projectId: Long) = Unauthenticated {
        Ok(finishedProjectService.findByProject(projectId))
    }

    def delete(id: Long) = AuthenticatedUser {
        if (finishedProjectService.delete(id)) {
            Ok
        } else {
            BadRequest
        }
    }
}

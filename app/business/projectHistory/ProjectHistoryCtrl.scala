package business.projectHistory

import javax.inject.Inject

import business.accounts.AccountService
import business.projects.ProjectService
import security.{AuthenticatedController, Authorization}

/**
  * Created by klaus on 01.06.17.
  */
class ProjectHistoryCtrl @Inject()(projectHistoryService: ProjectHistoryService, val accountService: AccountService) extends AuthenticatedController with Authorization {

    def findByProject(projectId: Long) = Unauthenticated {
        Ok(projectHistoryService.findByProject(projectId))
    }

    def findById(id: Long, historyId: Long) = Unauthenticated {
        projectHistoryService.byId(id) match {
            case Some(projectHistoryEntry) => Ok(projectHistoryEntry)
            case None => NotFound("No such project history entry")
        }
    }

    def create(id: Long) = AuthenticatedUser(parse.json[ProjectHistoryWriteModel]) { projectHistoryEntry =>
        projectHistoryService.create(projectHistoryEntry) match {
            case Some(newProjectHistoryEntry) => Ok(newProjectHistoryEntry)
            case None => BadRequest("Could not create")
        }
    }

}

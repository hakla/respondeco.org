package security

import business.accounts.{AccountPublicModel, AccountService}
import jp.t2v.lab.play2.auth.{AuthElement, LoginLogout}
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

trait AuthenticatedController extends Controller with LoginLogout with AuthConfigImpl with AuthElement {

    val accountService: AccountService

    def resolveUser(id: Id)(implicit context: ExecutionContext): Future[Option[User]] = Future(accountService.findByEmail(id))

    override def gotoLoginSucceeded(userId: String)(implicit request: RequestHeader, ctx: ExecutionContext): Future[Result] = {
        super.gotoLoginSucceeded(userId, resolveUser(userId) map { x =>
            play.api.mvc.Results.Ok(AccountPublicModel from x.get)
        })
    }
}


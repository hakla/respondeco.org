package authentication

import javax.inject.Inject

import business.accounts.AccountService
import jp.t2v.lab.play2.auth.LoginLogout
import play.api.mvc.Controller

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by Klaus on 18.11.2016.
  */
trait AuthenticatedController extends Controller with LoginLogout with Play2AuthConfig {

    val accountService: AccountService

    def resolveUser(id: Id)(implicit context: ExecutionContext): Future[Option[User]] = Future(accountService.findByEmail(id))

}

package authentication

import javax.inject.Inject

import business.accounts.{AccountNew, AccountService}
import common.ensure
import play.api.mvc.{Action, RequestHeader}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

/**
  * Created by Klaus on 08.12.2016.
  */
class AuthController @Inject()(val accountService: AccountService) extends AuthenticatedController {

    import common.AuthenticationStatus._

    def handleLogin = Action.async(parse.json[AccountNew]) { implicit request =>
        ensure that request.body is authenticated then {
            println("positive")
        } otherwise {
            println("otherwise")
        }

        if (request.body.password equals "test") {
            gotoLoginSucceeded(request.body.email)
        } else {
            authenticationFailed(request)
        }
    }

    def handleLogout = StackAction(AuthorityKey -> User) { implicit request: RequestHeader =>
        Await result(gotoLogoutSucceeded, Duration.Inf)
    }

}

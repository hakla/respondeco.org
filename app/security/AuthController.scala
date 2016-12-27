package security

import javax.inject.Inject

import business.accounts.{AccountAuthenticationTry, AccountService}
import security.Password.StringExtensions

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

/**
  * Created by Klaus on 08.12.2016.
  */
class AuthController @Inject()(val accountService: AccountService) extends AuthenticatedController with Authorization {

    def handleLogin = Unauthenticated(parse.json[AccountAuthenticationTry]) { (authenticationTry, request) =>
        val future = accountService findByEmail authenticationTry.user map { account =>
            authenticationTry.password.verifyPassword(account.password)
        } map { success =>
            if (success)
                gotoLoginSucceeded(authenticationTry.user)(request, global)
            else
                authenticationFailed(request)
        }

        Await result(future.get, Duration.Inf)
    }

    def handleLogout = StackAction(parse.empty, AuthorityKey -> User) { implicit request =>
        Await result(gotoLogoutSucceeded, Duration.Inf)
    }

}

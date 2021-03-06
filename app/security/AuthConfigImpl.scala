package security

import business.accounts.AccountModel
import common.AuthTokenAccessor
import jp.t2v.lab.play2.auth.{AuthConfig, TokenAccessor}
import play.Routes
import play.api.mvc.{RequestHeader, Result, Results}

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

/**
  * Created by Clemens Puehringer on 06/03/16.
  */
trait AuthConfigImpl extends AuthConfig {

    type Id = String

    type User = AccountModel

    type Authority = Role

    val idTag: ClassTag[Id] = scala.reflect.classTag[Id]

    val sessionTimeoutInSeconds: Int = 3600

    def loginSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] =
        Future.successful(Results.Redirect("/"))

    def logoutSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] =
        Future.successful(Results.Redirect("/login"))

    def authenticationFailed(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] =
        Future.successful(Results.Unauthorized)

    override def authorizationFailed(request: RequestHeader, user: User, authority: Option[Authority])(implicit context: ExecutionContext): Future[Result] = {
        Future.successful(Results.Forbidden("no permission"))
    }

    def authorize(user: User, authority: Authority)(implicit ctx: ExecutionContext): Future[Boolean] = Future.successful {
        (user.role, authority) match {
            case (Administrator, Administrator)     => true
            case (User, User)                       => true
            case (Administrator, User)              => true
            case _                                  => false
        }
    }

    override lazy val tokenAccessor: TokenAccessor = new AuthTokenAccessor()

}

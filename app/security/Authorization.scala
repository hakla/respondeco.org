package security

import jp.t2v.lab.play2.stackc.RequestWithAttributes
import play.api.mvc._

/**
  * Provides various helpers to check for authentication and authorization level
  */
trait Authorization {

    self: AuthenticatedController =>

    def Unauthenticated(block: => Result): Action[AnyContent] = Action { request => block }
    def Unauthenticated(block: Request[AnyContent] => Result): Action[AnyContent] = Action { request => block(request) }
    def Unauthenticated[A](bodyParser: BodyParser[A])(block: (A, Request[A]) => Result): Action[A] = Action[A](bodyParser) { request => block(request.body, request) }

    def AuthenticatedUser(block: RequestWithAttributes[AnyContent] => Result): Action[AnyContent] = StackAction(AuthorityKey -> User) { implicit request => block(request) }
    def AuthenticatedUser(block: => Result): Action[AnyContent] = StackAction(AuthorityKey -> User) { implicit request => block }
    def AuthenticatedUser[A](bodyParser: BodyParser[A])(block: A => Result): Action[A] = StackAction[A](bodyParser, AuthorityKey -> User) { implicit request => block(request.body) }

    def AuthenticatedAdmin(block: RequestWithAttributes[AnyContent] => Result): Action[AnyContent] = StackAction(AuthorityKey -> Administrator) { implicit request => block(request) }
    def AuthenticatedAdmin(block: => Result): Action[AnyContent] = StackAction(AuthorityKey -> Administrator) { implicit request => block }
    def AuthenticatedAdmin[A](bodyParser: BodyParser[A])(block: A => Result): Action[A] = StackAction[A](bodyParser, AuthorityKey -> Administrator) { implicit request => block(request.body) }

}

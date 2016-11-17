package common

import javax.inject._

import play.api.http.DefaultHttpErrorHandler
import play.api._
import play.api.mvc._
import play.api.routing.Router
import scala.concurrent._

@Singleton
class ErrorHandler @Inject() (env: Environment, config: Configuration, sourceMapper: OptionalSourceMapper, router: Provider[Router])
    extends DefaultHttpErrorHandler(env, config, sourceMapper, router) {

    override protected def onBadRequest(request: RequestHeader, message: String): Future[Result] = {
        super.onBadRequest(request, message)
    }

}

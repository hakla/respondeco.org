package common

import javax.inject.Inject

import play.Environment
import play.api.mvc._

class Application @Inject()(environment: Environment) extends Controller {

    def index = Action {
        Ok(views.html.index())
    }

    def app = Action {
        if (environment.isDev) {
            Ok(views.html.app_dev())
        } else {
            Ok(views.html.app())
        }
    }

    def admin = Action {
        if (environment.isDev) {
            Ok(views.html.admin_dev())
        } else {
            Ok(views.html.admin())
        }
    }

    def test = Action {
        Ok(""""test"""").withHeaders(("Content-type", "application/json"))
    }

}

package common

import javax.inject.Inject

import play.Environment
import play.api.Configuration
import play.api.mvc._

class Application @Inject()(environment: Environment, configuration: Configuration) extends Controller {

    val runInDev: Boolean = configuration.getBoolean("mode.dev").getOrElse(false)

    def index = Action {
        Ok(views.html.index())
    }

    def app = Action {
        if (runInDev) {
            Ok(views.html.app_dev())
        } else {
            Ok(views.html.app())
        }
    }

    def admin = Action {
        if (runInDev) {
            Ok(views.html.admin_dev())
        } else {
            Ok(views.html.admin())
        }
    }

    def test = Action {
        Ok(""""test"""").withHeaders(("Content-type", "application/json"))
    }

}

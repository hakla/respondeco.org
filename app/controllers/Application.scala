package controllers

import play.api.mvc._

class Application extends Controller {

    def index = Action {
        Ok(views.html.prod.index())
    }

    def app = Action {
        Ok(views.html.prod.app())
    }

    def admin = Action {
        Ok(views.html.admin())
    }

    def test = Action {
        Ok(""""test"""").withHeaders(("Content-type", "application/json"))
    }

}

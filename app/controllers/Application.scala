package controllers

import play.api.mvc._

class Application extends Controller {

    def index = Action {
        Ok(views.html.prod.index())
    }

    def app = Action {
        Ok(views.html.prod.app())
    }

    def asdf = Action {
        BadRequest("asdf")
    }

}

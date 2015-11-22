package controllers

import models.{OrganizationInsert, Organization}
import play.api.libs.json.{JsError, Json}
import play.api.mvc._

class OrganizationCtrl extends Controller {

    def findAll = Action {
        Ok(Json.toJson(Organization.findAll()))
    }

    def findById(id: Long) = Action {
        Ok(Json.toJson(Organization.findById(id)))
    }

    def insert = Action(parse.json) { request =>
        request.body.validate[OrganizationInsert].fold(
            errors => BadRequest(JsError.toFlatJson(errors)),
            organization => {
                val x = Organization.insert(organization)

                Ok(Json.toJson(x))
            }
        )
    }

    def update = Action(parse.json) { request =>
        request.body.validate[Organization].fold(
            errors => BadRequest(JsError.toFlatJson(errors)),
            organization => {
                val x = Organization.update(organization)

                Ok(Json.toJson(x))
            }
        )
    }

}

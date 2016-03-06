package controllers

import models.{OrganizationInsert, Organization}
import play.api.libs.json.{JsError, Json}
import play.api.mvc._

class OrganizationCtrl extends Controller {

    def findAll = Action {
        Ok(Json.toJson(Organization.findAll()))
    }

    def findById(id: Long) = Action {
        Organization.findById(id) match {
            case Some(org) => Ok(Json.toJson(org))
            case None => BadRequest("No such Organization")
        }
    }

    def update = Action(parse.json) { request =>
        request.body.validate[Organization].fold(
            errors => BadRequest(JsError.toFlatJson(errors)),
            organization => {
                Organization.update(organization) match {
                    case Some(org) => Ok(Json.toJson(org))
                    case None => BadRequest("Could not update")
                }
            }
        )
    }

}

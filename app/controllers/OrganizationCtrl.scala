package controllers

import com.google.inject.Inject
import models.{Organization, OrganizationInsert}
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

    def update(id: Long) = Action(parse.json) { request =>
        request.body.validate[OrganizationInsert].fold(
            errors => BadRequest(JsError.toJson(errors)),
            organization => {
                Organization.update(id, organization) match {
                    case Some(org) => Ok(Json.toJson(org))
                    case None => BadRequest("Could not update")
                }
            }
        )
    }

    def create = Action(parse.json) { request =>
        request.body.validate[OrganizationInsert].fold(
            errors => BadRequest(JsError.toJson(errors)),
            organization => {
                Organization.create(organization) match {
                    case Some(org) => Ok(Json.toJson(org))
                    case None => BadRequest("Could not create")
                }
            }
        )
    }

}

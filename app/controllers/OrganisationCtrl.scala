package controllers

import models.{Organization, OrganizationInsert}
import play.api.libs.json.Json
import play.api.mvc._

class OrganisationCtrl extends Controller {

    def findAll = Action {
        Ok(Json.toJson(Organization.findAll()))
    }

    def findById(id: Long) = Action {
        Organization.findById(id) match {
            case Some(org) => Ok(Json.toJson(org))
            case None => BadRequest("No such Organization")
        }
    }

    def update(id: Long) = Action(parse.json[OrganizationInsert]) { request =>
        Organization.update(id, request.body) match {
            case Some(org) => Ok(Json.toJson(org))
            case None => BadRequest("Could not update")
        }
    }

    def create = Action(parse.json[OrganizationInsert]) { request =>
        Organization.create(request.body) match {
            case Some(org) => Ok(Json.toJson(org))
            case None => BadRequest("Could not create")
        }
    }

}

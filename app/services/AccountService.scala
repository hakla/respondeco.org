package services

import java.sql.Connection

import exceptions.IllegalValueException
import models._
import play.api.db.DB
import play.api.Play.current
import play.db.Database

import scala.util.Try

/**
  * Created by Clemens Puehringer on 28/11/15.
  */
class AccountService {

    def create(account: AccountNew): Try[Account] = {
        Try({
            Account.findByEmail(account.email) match {
                case Some(acc) => throw new IllegalValueException("Account already exists")
                case _ => true
            }
            Organization.findByName(account.organizationName) match {
                case Some(org) => throw new IllegalArgumentException("Organization already exists")
                case _ => true
            }
            DB.withTransaction { connection =>
                val organization : Organization = {
                    Organization.create(OrganizationInsert(account.organizationName))(connection) match {
                        case None => throw new IllegalArgumentException("Could not insert organization")
                        case Some(org) => org
                    }
                }
                Account.create(account.email, account.password, organization.id)(connection) match {
                    case None => throw new IllegalArgumentException("Could not insert account")
                    case Some(acc) => acc
                }
            }
        })
    }

}

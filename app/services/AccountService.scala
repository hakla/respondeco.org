package services

import exceptions.IllegalValueException
import models._
import play.api.db.DB
import play.api.Play.current

import scala.util.Try

/**
  * Created by Clemens Puehringer on 28/11/15.
  */
class AccountService {

    def create(account: AccountCreate): Try[AccountPublic] = {
        Try({
            Account.findByEmail(account.email) match {
                case Some(acc) => throw new IllegalValueException("Account already exists")
                case _ => true
            }
            Organization.findByName(account.organizationName) match {
                case Some(org) => throw new IllegalArgumentException("Organization already exists")
                case _ => true
            }
            DB.withTransaction { implicit connection =>
                val organization = Organization.insert(OrganizationInsert(account.organizationName)) match {
                    case None => throw new IllegalArgumentException("Could not insert organization")
                    case Some(org) => org
                }
                Account.create(AccountInsert(account.email, account.password, organization.id)) match {
                    case None => throw new IllegalArgumentException("Could not insert account")
                    case Some(acc) => acc
                }
            }
        })
    }

//    def update(account: AccountUpdate): Try[AccountPublic] = {
//        Try({
//            Account
//        })
//    }

}

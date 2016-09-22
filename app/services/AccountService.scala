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

    def update(id: Long, account: AccountPublic): Try[Account] = {
        Try({
            Account.update(id, account) match {
                case None => throw new IllegalArgumentException("Account with id doesn't exist")
                case Some(account) => account
            }
        })
    }

    def findAll() : Seq[Account] = {
        Account.findAll()
    }


    def create(account: AccountNew): Try[Account] = {
        Try({
            Account.findByEmail(account.email) match {
                case Some(acc) => throw new IllegalValueException("Account already exists")
                case _ => true
            }
            DB.withTransaction { connection =>
                Account.create(account.email, account.password)(connection) match {
                    case None => throw new IllegalArgumentException("Could not insert account")
                    case Some(acc) => acc
                }
            }
        })
    }

}

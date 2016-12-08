package common

import business.accounts.AccountNew

/**
  * Just some stuff to play around with, you know. Just to acquire some Scala know-how
  */
class EnsureAuthentication(val accountNew: AccountNew) {

    def is(authenticationStatus: Int): EnsureAuthentication = {
        val correctPassword = "test".equals(accountNew.password)

        if (authenticationStatus == AuthenticationStatus.authenticated) {
            if (correctPassword) new PositiveAuthentication(accountNew)
            else new NegativeAuthentication(accountNew)
        } else {
            if (correctPassword) new NegativeAuthentication(accountNew)
            else new PositiveAuthentication(accountNew)
        }
    }

    def then(block: => Unit): EnsureAuthentication = {
        block
        this
    }

    def otherwise(block: => Unit): EnsureAuthentication = {
        block
        this
    }

}

class PositiveAuthentication(accountNew: AccountNew) extends EnsureAuthentication(accountNew) {

    override def otherwise(block: => Unit): EnsureAuthentication = this

}

class NegativeAuthentication(accountNew: AccountNew) extends EnsureAuthentication(accountNew) {

    override def then(block: => Unit): EnsureAuthentication = this

}

object AuthenticationStatus extends Enumeration {

    implicit val authenticated = 1
    implicit val unauthenticated = 2

}

object ensure {

    def that(accountNew: AccountNew) = new EnsureAuthentication(accountNew)

}

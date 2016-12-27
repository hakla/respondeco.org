package common

import business.accounts.AccountAuthenticationTry

/**
  * Just some stuff to play around with, you know. Just to acquire some Scala know-how
  */
class EnsureAuthentication(val account: AccountAuthenticationTry) {

    def is(authenticationStatus: Int): EnsureAuthentication = {
        val correctPassword = "test".equals(account.password)

        if (authenticationStatus == AuthenticationStatus.authenticated) {
            if (correctPassword) new PositiveAuthentication(account)
            else new NegativeAuthentication(account)
        } else {
            if (correctPassword) new NegativeAuthentication(account)
            else new PositiveAuthentication(account)
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

class PositiveAuthentication(acocunt: AccountAuthenticationTry) extends EnsureAuthentication(acocunt) {

    override def otherwise(block: => Unit): EnsureAuthentication = this

}

class NegativeAuthentication(account: AccountAuthenticationTry) extends EnsureAuthentication(account) {

    override def then(block: => Unit): EnsureAuthentication = this

}

object AuthenticationStatus extends Enumeration {

    implicit val authenticated = 1
    implicit val unauthenticated = 2

}

object ensure {

    def that(account: AccountAuthenticationTry) = new EnsureAuthentication(account)

}

package security

import de.mkammerer.argon2.{Argon2, Argon2Factory}
import security.Password.{HashedPassword, UnhashedPassword}

/**
  * Created by Klaus on 24.12.2016.
  */

object Password {

    implicit class StringExtensions(password: UnhashedPassword) {

        def hashedPassword: HashedPassword = Password.hash(password)

        def verifyPassword(hash: HashedPassword): Boolean = Password.verify(password, hash)

    }

    type UnhashedPassword = String
    type HashedPassword = String

    private val SALT_LENGTH: Int = 128
    private val HASH_LENGTH: Int = 128

    private def argon2: Argon2 = Argon2Factory.create(SALT_LENGTH, HASH_LENGTH)

    def hash(password: UnhashedPassword): HashedPassword = argon2.hash(2, 65536, 4, password)

    def verify(password: UnhashedPassword, hash: HashedPassword): Boolean = argon2.verify(hash, password)

}

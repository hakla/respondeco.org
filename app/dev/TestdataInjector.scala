package dev

import javax.inject.{Inject, Singleton}

import anorm._
import security.{Password, User}
import common.{Database, MyWriteable}
import org.yaml.snakeyaml.Yaml
import play.api.inject.ApplicationLifecycle
import play.api.libs.json.Json
import play.api.{Application, Configuration}
import security.Password.StringExtensions

/**
  * Created by Klaus on 23.12.2016.
  */
case class User(email: String, name: String, password: String, role: Int)

object User extends MyWriteable[User] {
    implicit val formatter = Json.format[User]
}

case class Testdata(users: Seq[User])

object Testdata extends MyWriteable[Testdata] {
    implicit val formatter = Json.format[Testdata]
}

@Singleton
class TestdataInjector @Inject()(applicationLifecycle: ApplicationLifecycle, configuration: Configuration, application: Application, implicit val db: Database) {

    val isEnabled: Boolean = configuration.getBoolean("testdata.enabled").getOrElse(false)

    private def testdata: Testdata = {
        val yaml: Yaml = new Yaml()
        val file: String = configuration.getString("testdata.data").getOrElse("testdata/default.json")

        Json.parse(
            application.resourceAsStream(file) match {
                case Some(stream) => stream
                case None => throw new Exception(s"Couldn't find $file")
            }
        ).validate[Testdata].asEither match {
            case Right(testdata) => testdata
            case Left(x) => throw new Exception("Couldn't parse testdata\n: " + x)
        }
    }

    private def dataAvailable: Boolean = db.withConnection { implicit connection =>
        SQL("SELECT COUNT(*) FROM account").as(SqlParser.int(1).single) > 0
    }

    private def insert(testdata: Testdata) = db.withConnection { implicit connection =>
        for (user <- testdata.users) {
            SQL("insert into account (email , name, password, role) values({email}, {name}, {password}, {role})").on(
                'email -> user.email,
                'name -> user.name,
                'password -> user.password.hashedPassword,
                'role -> user.role
            ).executeInsert()
        }
    }

    if (isEnabled && !dataAvailable) {
        insert(testdata)
    }

}

package common

import java.sql.Connection
import javax.inject.Inject

import play.api.db.DBApi

/**
  * Created by Klaus on 17.11.2016.
  */
class Database @Inject() (db: DBApi) {

    def withConnection[A](block: Connection => A): A = {
        db.database("default").withConnection(block)
    }

}

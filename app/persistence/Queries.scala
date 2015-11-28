package persistence

import anorm._

import java.sql.Connection

/**
  * Created by Clemens Puehringer on 28/11/15.
  */
class Queries[T](table: String, parser: RowParser[T]) {

    def findById(id: Long)(implicit connection: Connection): Option[T] = {
        Statements.FIND_BY_ID.on(
            'id -> id,
            'table -> table
        ).executeQuery().as(parser.*) match {
            case x::_ => Some(x)
            case _ => None
        }
    }

}

object Statements {

    def FIND_BY_ID = SQL("SELECT * FROM {table} WHERE id = {id}")

}

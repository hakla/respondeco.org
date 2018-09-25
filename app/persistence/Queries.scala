package persistence

import anorm._
import common.Database

import scala.reflect.ClassTag
import scala.reflect.runtime.{universe => ru}

abstract class Queries[A: ClassTag] {

    implicit val parser: RowParser[A]
    implicit val db: Database
    val table: String

    def all: List[A] = all("asc")

    def all(namedParameter: NamedParameter*): List[A] = all("asc", namedParameter:_*)

    def all(order: String, namedParameter: NamedParameter*): List[A] = db.withConnection { implicit connection =>
        where(order, namedParameter).executeQuery().as(parser.*)
    }

    def byId(id: Long): Option[A] = first('id -> id)

    def delete(id: Long): Boolean = db.withConnection { implicit c =>
        SQL(s"delete from $table where id = {id}").on(
            'id -> id
        ).executeUpdate() == 1
    }

    def first(parameters: NamedParameter*): Option[A] = db.withConnection { implicit connection =>
        where(parameters:_*).executeQuery().as(parser.*) match {
            case x :: _ => Some(x)
            case _ => None
        }
    }

    protected def where(parameters: NamedParameter*): SimpleSql[Row] = where("asc", parameters)

    protected def where(order: String, parameters: Seq[NamedParameter]): SimpleSql[Row] = {
        val statement: String = s"SELECT * FROM $table" + (parameters.nonEmpty match {
            case true =>
                val x: Seq[String] = parameters.map { x =>
                    s"${x.name} = {${x.name}}"
                }

                val and = x.mkString(" AND ")

                s" WHERE $and"
            case false => ""
        })

        var query: SimpleSql[Row] = SQL(statement + s" ORDER BY id $order")

        parameters.foldLeft(query)((query, b) => query.on(b))
    }

}

object Statements {

    def FIND_BY_ID = SQL"SELECT * FROM {table} WHERE id = {id}"

    def FIND_ALL = SQL"SELECT * FROM {table}"

}

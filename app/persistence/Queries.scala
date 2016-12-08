package persistence

import anorm._
import common.Database

import scala.reflect.{ClassTag, _}

/**
  * Created by Clemens Puehringer on 28/11/15.
  */
abstract class Queries[A: ClassTag] {

    implicit val parser: RowParser[A]
    implicit val db: Database
    val table: String = classTag[A].runtimeClass.getSimpleName.toLowerCase()

    def all(namedParameter: NamedParameter*): List[A] = db.withConnection { implicit connection =>
        where(namedParameter).executeQuery().as(parser.*)
    }

    def first(parameters: NamedParameter*): Option[A] = db.withConnection { implicit connection =>
        where(parameters).executeQuery().as(parser.*) match {
            case x :: _ => Some(x)
            case _ => None
        }
    }

    def byId(id: Long): Option[A] = db.withConnection { implicit connection =>
        first(
            'id -> id
        )
    }

    private def where(parameters: NamedParameter*): SimpleSql[Row] = {
        where(parameters)
    }

    private def where(parameters: Seq[NamedParameter]): SimpleSql[Row] = {
        val statement: String = s"SELECT * FROM $table" + (parameters.nonEmpty match {
            case true =>
                val x: Seq[String] = parameters.map { x =>
                    s"${x.name} = {${x.name}}"
                }

                val and = x.mkString(" AND ")

                s" WHERE $and"
            case false => ""
        })

        var query: SimpleSql[Row] = SQL(statement)

        parameters.foldLeft(query)((query, b) => query.on(b))
    }

}

object Where {

}

object Statements {

    def FIND_BY_ID = SQL"SELECT * FROM {table} WHERE id = {id}"

    def FIND_ALL = SQL"SELECT * FROM {table}"

}

package common

import play.api.libs.json.Json.toJson
import play.api.libs.json._
import play.api.mvc.Request

case class Paginated[T](items: List[T], page: Int, pageSize: Int, total: Int)

object Paginated {

    implicit def paginatedWrites[T](implicit fmt: Writes[T]): Writes[Paginated[T]] = new Writes[Paginated[T]] {
        def writes(ts: Paginated[T]) = JsObject(Seq(
            "page" -> JsNumber(ts.page),
            "pageSize" -> JsNumber(ts.pageSize),
            "total" -> JsNumber(ts.total),
            "items" -> JsArray(ts.items.map(toJson(_)))
        ))
    }

}

trait Pagination {

    def paginated[A](request: Request[_], items: List[A]): Paginated[A] = {
        val pageSize: Int = request.queryString.get("pageSize").flatMap(pageSize => pageSize.headOption.map(_.toInt)).getOrElse(items.length)
        val page: Int = request.queryString.get("page").flatMap(page => page.headOption.map(_.toInt)).getOrElse(1)

        Paginated(
            items = items.slice((page - 1) * pageSize, page * pageSize),
            page = page,
            pageSize = pageSize,
            total = items.length
        )
    }

}

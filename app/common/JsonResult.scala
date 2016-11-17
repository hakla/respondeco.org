package common

import play.api.http.HttpEntity
import play.api.mvc.{ResponseHeader, Result}

/**
  * Created by Klaus on 17.11.2016.
  */
class JsonResult(header: ResponseHeader, body: HttpEntity) extends Result(header, body) {



}

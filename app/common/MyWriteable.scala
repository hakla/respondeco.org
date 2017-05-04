package common

import play.api.http.{ContentTypes, Writeable}
import play.api.libs.json.{Format, Json}
import play.api.mvc.Codec

import scala.language.experimental.macros

/**
  * Created by Klaus on 17.11.2016.
  */
trait MyWriteable[A] {

    implicit val formatter: Format[A]

    implicit def writeable(implicit codec: Codec): Writeable[A] =
        Writeable(result => codec.encode(Json.toJson(result).toString()), Some(ContentTypes.JSON))

    implicit def writeableList(implicit codec: Codec): Writeable[List[A]] =
        Writeable(result => codec.encode(Json.toJson(result).toString()), Some(ContentTypes.JSON))

}

package common

import jp.t2v.lab.play2.auth.{AuthenticityToken, TokenAccessor}
import play.api.mvc.Results._
import play.api.mvc.{RequestHeader, Result}

/**
  * Created by Klaus on 08.12.2016.
  */
class AuthTokenAccessor extends TokenAccessor {

    override def extract(request: RequestHeader): Option[AuthenticityToken] = {
        request.headers.get("X-Access-Token") match {
            case Some(token) =>
                verifyHmac(token)
            case None => None
        }
    }

    override def put(token: AuthenticityToken)(result: Result)(implicit request: RequestHeader): Result = {
        result.withHeaders(
            "X-Access-Token" -> sign(token)
        )
    }

    override def delete(result: Result)(implicit request: RequestHeader): Result = Ok

}

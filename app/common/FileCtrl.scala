package common

import javax.inject.Inject

import business.accounts.AccountService
import play.api.libs.Files
import play.api.mvc.{Action, MultipartFormData}
import se.digiplant.res.api.Res
import security.{AuthenticatedController, Authorization}

class FileCtrl @Inject() (val accountService: AccountService, res: Res) extends AuthenticatedController with Authorization {

    def uploadImage(): Action[MultipartFormData[Files.TemporaryFile]] = AuthenticatedUser(parse.multipartFormData) { request =>
        request.file("file").map { (file: MultipartFormData.FilePart[Files.TemporaryFile]) =>
            val fileType: Option[String] = file.contentType.map { contentType => contentType.split("/")(1) }

            Ok(res.put(file, "tmp", Seq.empty))
        } getOrElse BadRequest
    }

}

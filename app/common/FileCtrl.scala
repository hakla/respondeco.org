package common

import javax.inject.Inject

import business.accounts.AccountService
import play.api.Logger
import play.api.libs.Files
import play.api.mvc.{Action, MultipartFormData}
import se.digiplant.res.api.Res
import security.{AuthenticatedController, Authorization}

class FileCtrl @Inject() (val accountService: AccountService, res: Res) extends AuthenticatedController with Authorization {

    val logger: Logger = Logger(this.getClass)

    def uploadImage(): Action[MultipartFormData[Files.TemporaryFile]] = AuthenticatedUser(parse.multipartFormData) { (body, request) =>
        logger.info(s"""Uploading file for ${loggedIn(request).name}""")

        body.file("file").map { (file: MultipartFormData.FilePart[Files.TemporaryFile]) =>
            val fileType: Option[String] = file.contentType.map { contentType => contentType.split("/")(1) }
            val resource = res.put(file, "tmp", Seq.empty)

            logger.info(s"""Created file ${file.filename}""")

            Ok(resource)
        } getOrElse BadRequest
    }

}

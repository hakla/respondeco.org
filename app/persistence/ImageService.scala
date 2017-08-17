package persistence

import anorm.SQL
import common.CrudService

/**
  * Created by klaus on 17.08.17.
  */
trait ImageService[A, B] {

    self: CrudService[A, B] =>

    def updateImage(id: Long, imageId: String): Boolean = db.withConnection { implicit connection =>
        SQL(s"update $table set image = {imageId} where id = {id}").on(
            'id -> id,
            'imageId -> imageId
        ).executeUpdate() == 1
    }

}

package common

import persistence.Queries

/**
  * Created by Klaus on 12.03.2017.
  */
trait CrudService[A, B] extends Queries[A] {

    val table: String

    def create (insert: B): Option[A]
    def update (id: Long, insert: B): Option[A]

}

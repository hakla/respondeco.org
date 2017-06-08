package business.rating

import javax.inject.Inject

import anorm.{Macro, RowParser, SQL}
import common.Database
import persistence.Queries

/**
  * Created by klaus on 01.06.17.
  */
class RatingService @Inject()(implicit val db: Database) extends Queries[RatingModel] {

    implicit val parser: RowParser[RatingModel] = Macro.namedParser[RatingModel]
    implicit val table: String = "rating"

    def create(rating: RatingWriteModel) : Option[RatingModel] = db.withConnection { implicit c =>
        SQL(s"insert into $table (id, liked, testimonial) values (null, {liked}, {testimonial})").on(
            'liked -> rating.liked,
            'testimonial -> rating.testimonial
        ).executeInsert().asInstanceOf[Option[Long]].flatMap(byId)
    }

    def update(rating: RatingWriteModel): Option[RatingModel] = update(rating.id.get, rating)

    def update(id: Long, rating: RatingWriteModel): Option[RatingModel] = db.withConnection { implicit c =>
        SQL(s"update $table set id = {id}, liked = {liked}, testimonial = {testimonial} where id = {id}").on(
            'id -> id,
            'liked -> rating.liked,
            'testimonial -> rating.testimonial
        ).executeUpdate() match {
            case 1 => byId(id)
            case _ => None
        }
    }

}

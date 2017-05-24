package business.projects

import javax.inject.Inject

import anorm.{Macro, RowParser, _}
import common.Database
import persistence.Queries

/**
  * Created by Klaus on 17.11.2016.
  */
class ProjectService @Inject()(implicit val db: Database) extends Queries[ProjectModel] {

    implicit val parser: RowParser[ProjectModel] = Macro.namedParser[ProjectModel]
    implicit val table: String = "project"

    def create(project: ProjectWriteModel) : Option[ProjectModel] = db.withConnection { implicit c =>
        SQL(s"insert into $table (id, name, location, description, category, subcategory, start, end, benefits, price, organisation) values(null, {name}, {location}, {description}, {category}, {subcategory}, {start}, {end}, {benefits}, {price}, {organisation})").on(
            'name -> project.name,
            'location -> project.location,
            'description -> project.description,
            'category -> project.category,
            'subcategory -> project.subcategory,
            'start -> project.start.map(_.atStartOfDay),
            'end -> project.end.map(_.atStartOfDay),
            'benefits -> project.benefits,
            'price -> project.price,
            'organisation -> project.organisation
        ).executeInsert().asInstanceOf[Option[Long]].map(ProjectModel.fromWriteModel(_, project))
    }

    def update(id: Long, project: ProjectWriteModel): Option[ProjectModel] = db.withConnection { implicit c =>
        SQL(s"update $table set id = {id}, name = {name}, location = {location}, description = {description}, category = {category}, subcategory = {subcategory}, start = {start}, end = {end}, benefits = {benefits}, price = {price}, organisation = {organisation} where id = {id}").on(
            'id -> id,
            'name -> project.name,
            'location -> project.location,
            'description -> project.description,
            'category -> project.category,
            'subcategory -> project.subcategory,
            'start -> project.start.map(_.atStartOfDay),
            'end -> project.end.map(_.atStartOfDay),
            'benefits -> project.benefits,
            'price -> project.price,
            'organisation -> project.organisation
        ).executeUpdate() match {
            case 1 => byId(id)
            case _ => None
        }
    }

    def findByName(name: String): Option[ProjectModel] = db.withConnection { implicit c =>
        first('name -> name)
    }

}

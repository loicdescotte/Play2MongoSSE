package models

import play.api.libs.json._
import reactivemongo.bson._
import play.modules.reactivemongo.json.BSONFormats._

case class Post(author: String, message: String)

object Post {
  implicit val postFormat = Json.format[Post]
}
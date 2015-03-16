package models

import play.api.libs.json._
import reactivemongo.bson._
import play.modules.reactivemongo.json.BSONFormats._

case class Post(_id: Option[BSONObjectID]=None, author: String, message: String)

object Post {
  implicit val postFormat = Json.format[Post]
  def build(author: String, message: String):Post = Post(None, author, message)
  def unBuild(post: Post):Option[(String, String)] = Some(post.author, post.message)
}
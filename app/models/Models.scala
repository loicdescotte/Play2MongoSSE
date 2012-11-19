package models

import reactivemongo.bson._
import reactivemongo.bson.handlers._

case class Post(var id: Option[BSONObjectID], author: String, message: String)

object Post {  
  
  def build(id: Option[String], author: String, message: String) = {
    Post(id.map(new BSONObjectID(_)), author, message)
  }

  def unbuild(post: Post) = {
    Some((post.id.map(_.stringify), post.author, post.message))
  }   
}

object PostBSONReader extends BSONReader[Post] {
    def fromBSON(document: BSONDocument): Post = {
      val doc = document.toTraversable
      Post(
        doc.getAs[BSONObjectID]("_id"),
        doc.getAs[BSONString]("author").get.value,
        doc.getAs[BSONString]("message").get.value
      )
    }
}

object PostBSONWriter extends BSONWriter[Post] {
    def toBSON(post: Post) = {
      BSONDocument(
        "_id" -> post.id.getOrElse(BSONObjectID.generate),
        "author" -> BSONString(post.author),
        "message" -> BSONString(post.message)
      )
    }
}
package controllers

import scala.concurrent.{ ExecutionContext, Future }

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.Play.current
import play.api.data.format.Formats._
import play.api.libs.json._
import models._
import views._

import reactivemongo.api._
import reactivemongo.bson._
import reactivemongo.bson.handlers.DefaultBSONHandlers._

import play.modules.reactivemongo._

import libs.iteratee._
import libs.EventSource
import libs.json.JsValue

object Application extends Controller with MongoController {

  val db = ReactiveMongoPlugin.db
  val collection = db.collection("postsMongoCollection")
  collection.createCapped(1024 * 1024, None)

  val postForm = Form(
    mapping(
      "id" -> optional(of[String]),
      "author" -> nonEmptyText,
      "message" -> nonEmptyText)(Post.build)(Post.unbuild))

  def index = Action { Home }

  def Home = Redirect(routes.Application.list)

  def list = Action { implicit request =>
    implicit val reader = PostBSONReader
    // Async resturns an AsyncResult from a Promise
    Async {
      //find all docs
      val found = collection.find(BSONDocument()).toList
      found.map { posts =>
        Ok(html.list(posts, postForm))
      }
    }
  }

  implicit val postJsonWrites = new Writes[Post] {
    def writes(post: Post): JsValue = {
      Json.obj(
        "author" -> post.author,
        "message" -> post.message)
    }
  }

  def listJson = Action {
    implicit val bsonReader = PostBSONReader
    Async {
      val found = collection.find(BSONDocument()).toList
      found map {
        posts => Ok(Json.toJson(posts))
      }
    }
  }

  def searchPage(filter: String) = Action {
    Ok(html.searchResults(filter))
  }

  def search(filter: String) = Action {

    import play.modules.reactivemongo.PlayBsonImplicits._

    Logger.info("filter : " + filter)

    val query = QueryBuilder().query(BSONDocument("message" -> BSONRegex(filter, "")))
    //query results asynchronous cursor
    val cursor = collection.find[JsValue](query, QueryOpts().tailable.awaitData)

    //create the enumerator
    val dataProducer = cursor.enumerate

    //console output
    //dataProducer.apply(Iteratee.foreach {doc =>  println("found document: " + doc)})

    //stream the results
    Ok.stream(dataProducer &> EventSource()).as("text/event-stream")
  }

  def edit(id: String) = Action { implicit request =>
    implicit val bsonReader = PostBSONReader
    Async {
      val objectId = new BSONObjectID(id)
      val cursor = collection.find(BSONDocument("_id" -> objectId))
      // promise of option of post!
      cursor.headOption.map {
        result =>
          result.map {
            post => Ok(html.edit(id, postForm.fill(post)))
          }.getOrElse(NotFound)
      }
    }
  }

  def create() = Action { implicit request =>
    implicit val bsonWriter = PostBSONWriter
    //no validation here (just an example :)
    val post = postForm.bindFromRequest.get
    AsyncResult {
      collection.insert(post).map(_ => Home)
    }
  }

  def update(id: String) = Action { implicit request =>
    postForm.bindFromRequest.fold(
      //return to edit page with entered values
      errors => Ok(views.html.edit(id, errors)),
      post => AsyncResult {
        val objectId = new BSONObjectID(id)
        val modifier = BSONDocument(
          "$set" -> BSONDocument(
            "author" -> BSONString(post.author),
            "message" -> BSONString(post.message)))
        collection.update(BSONDocument("_id" -> objectId), modifier).map { _ =>
          Home
        }
      })
  }

}
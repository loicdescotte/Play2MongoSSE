package controllers

import scala.concurrent.{ ExecutionContext, Future }

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.Play.current
import play.api.data.format.Formats._
import play.api.libs.json._
import play.api.mvc.WebSocket
import models._
import views._

import reactivemongo.api._
import reactivemongo.bson._

import play.api.libs.json._

import play.modules.reactivemongo._
import play.modules.reactivemongo.json.collection.JSONCollection
import play.modules.reactivemongo.json.BSONFormats._

import libs.iteratee._
import libs.EventSource
import libs.json.JsValue
import libs.json.Json

import scala.concurrent.ExecutionContext.Implicits.global

object HtmlController extends MongoSSEApplication {

  val postForm = Form(
    mapping(
      "author" -> nonEmptyText,
      "message" -> nonEmptyText
    )(Post.apply)(Post.unapply)
  )

  def index = Action { Home }

  def Home = Redirect(routes.HtmlController.list)

  def list = Action.async { implicit request =>    
    //find all docs
    val furturePosts = collection.find(Json.obj())
      .cursor[Post]
      .collect[List]()
      
      furturePosts.map { posts =>
        Ok(html.list(posts, postForm))      
    }
  }

  def searchPage(filter: String) = Action {
    Ok(html.searchResults(filter))
  }

  def searchPageWS(filter: String) = Action { implicit request =>
    Ok(html.searchResultsWS(filter))
  }

  /*def edit(id: String) = Action { implicit request =>
      val objectId = new BSONObjectID(id)
      val cursor = collection.find(Json.obj("_id" -> objectId))
      // promise of option of post
      cursor.headOption.map {
        result =>
          result.map {
            post => Ok(html.edit(id, postForm.fill(post)))
          }.getOrElse(NotFound)
      }
  }*/

  def create() = Action.async { implicit request =>
    //no validation here (just an example :)
    val post = postForm.bindFromRequest.get
      collection.insert(post).map(_ => Home)
  }

/*
  def update(id: String) = Action { implicit request =>
    postForm.bindFromRequest.fold(
      //return to edit page with entered values
      errors => Ok(views.html.edit(id, errors)),
      post => {
        val objectId = new BSONObjectID(id)
        val modifier = BSONDocument(
          "$set" -> BSONDocument(
            "author" -> BSONString(post.author),
            "message" -> BSONString(post.message)))
        collection.update(BSONDocument("_id" -> objectId), modifier).map { _ =>
          Home
        }
      })
  }*/

}

object JsonController extends MongoSSEApplication {

  def listJson = Action.async {     
    //automatic BSON to JSON conversion (via play default implicits)
    val found = collection.find[JsValue](Json.obj()).cursor[Post].collect[List]()
    found map {
      posts => Ok(Json.toJson(posts))
    }
  }

  def search(filter: String) = Action {
    Logger.info("filter : " + filter)
    val query = Json.obj("message" -> BSONRegex(filter, ""))
    //query results asynchronous cursor
    val cursor = collection.find[JsValue](query).options(QueryOpts().tailable.awaitData).cursor[JsValue]
    //create the enumerator
    val dataProducer = cursor.enumerate(Int.MaxValue)
    //stream the results
    Ok.chunked(dataProducer &> EventSource()).as("text/event-stream")
  }


  def searchWS =  WebSocket.using[JsValue] { request => 

    // out enumerator will be fed from the channel
    val (out, channel) = Concurrent.broadcast[(JsValue)]

    val in = Iteratee.foreach[JsValue]{ msg => 

      val filter = (msg \ "filter").as[JsString].value
      Logger.info(filter)  

      //query results asynchronous cursor
      val query = Json.obj("message" -> BSONRegex(filter, ""))
      val cursor = collection.find[JsValue](query).options(QueryOpts().tailable.awaitData).cursor[JsValue]
         
      //stream the results : push each element from the curson in the channel
      val cursorEnumerator = cursor.enumerate(Int.MaxValue)
      cursorEnumerator.run(Iteratee.foreach{result => channel push result})
    
    }
  
    (in, out)
  }

}

class MongoSSEApplication extends Controller with MongoController {
  override val db = ReactiveMongoPlugin.db
  val collection = db.collection[JSONCollection]("postsMongoCollection")
  collection.createCapped(1024 * 1024, None)
}
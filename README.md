A Play Framework demo app to push data from database to browser in realtime with reactivemongo and server sent events (or WebSockets)

This controller method pushes new data from mongo in live to a SSE socket : 

```scala
 def search(filter: String) = Action { 
    val query = QueryBuilder().query(BSONDocument("message" -> BSONRegex(filter, "")))

    //query results asynchronous cursor
    val cursor = collection.find[JsValue](query, QueryOpts().tailable.awaitData)

    //create the enumerator
    val dataProducer = cursor.enumerate

    //stream the results
    Ok.chunked(dataProducer through EventSource()).as("text/event-stream")
}

```
Then the client is able to display new entries as soon as they are inserted in database (no pull needed from browser)

This application contains some JSON serialization and client side HTML5 validation samples too

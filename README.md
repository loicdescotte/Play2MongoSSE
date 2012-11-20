A Play 2.1 demo app to push data from database to browser in realtime with reactivemongo and server side events

This application contains some JSON serialization and client side HTML5 validation samples too

This controller method pushes new data from mongo in live to a SSE socket : 

```scala
 def search(filter: String) = Action { 
    //[...]
    val query = QueryBuilder().query(BSONDocument("message" -> BSONRegex(filter, "")))

    //query results asynchronous cursor
    val cursor = collection.find[JsValue](query, QueryOpts().tailable.awaitData)

    //create the enumerator
    val dataProducer = cursor.enumerate

    //stream the results
    Ok.stream(dataProducer through EventSource()).as("text/event-stream")
}

```

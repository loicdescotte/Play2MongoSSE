name := "Play2MongoSSE"

fork in Test := false

scalaVersion := "2.11.4"

version := "1.0.0-SNAPSHOT"

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += Resolver.sonatypeRepo("snapshots")

resolvers += "sgodbillon" at "https://bitbucket.org/sgodbillon/repository/raw/master/snapshots/"

resolvers += Resolver.url("github repo for html5tags", url("http://loicdescotte.github.io/Play2-HTML5Tags/releases/"))(Resolver.ivyStylePatterns)

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  cache,  
  ws,  
  "com.loicdescotte.coffeebean" %% "html5tags" % "1.2.1",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.5.0.akka23"
)
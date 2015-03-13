name := "Play2MongoSSE"

fork in Test := false

scalaVersion := "2.10.0"

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
  "reactivemongo" %% "reactivemongo" % "0.1-SNAPSHOT" cross CrossVersion.full,
  "play.modules.reactivemongo" %% "play2-reactivemongo" % "0.1-SNAPSHOT" cross CrossVersion.full
)
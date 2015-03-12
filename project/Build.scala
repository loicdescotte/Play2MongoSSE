import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "feedr"
    val appVersion      = "1.0-SNAPSHOT"
    val scalaVersion    = "2.10.0"

    val appDependencies = Seq(
       "com.loicdescotte.coffeebean" % "html5tags_2.10" % "1.0",
       "reactivemongo" %% "reactivemongo" % "0.1-SNAPSHOT" cross CrossVersion.full,
       "play.modules.reactivemongo" %% "play2-reactivemongo" % "0.1-SNAPSHOT" cross CrossVersion.full
    )

    val main = play.Project(appName, appVersion, appDependencies).settings(
     resolvers ++= Seq(
      "sgodbillon" at "https://bitbucket.org/sgodbillon/repository/raw/master/snapshots/",
      Resolver.url("github repo for html5tags", url("http://loicdescotte.github.io/Play2-HTML5Tags/releases/"))(Resolver.ivyStylePatterns)
    )
   )

}

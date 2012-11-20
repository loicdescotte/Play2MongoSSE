import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "feedr"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "com.loicdescotte.coffeebean" % "html5tags_2.10" % "1.0-SNAPSHOT",
       "reactivemongo" %% "reactivemongo" % "0.1-SNAPSHOT",
       "play.modules.reactivemongo" %% "play2-reactivemongo" % "0.1-SNAPSHOT"
    )

    val main = play.Project(appName, appVersion, appDependencies).settings(
     resolvers ++= Seq(
      "sgodbillon" at "https://bitbucket.org/sgodbillon/repository/raw/master/snapshots/"
    )
   )

}

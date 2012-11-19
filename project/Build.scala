import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "feedr"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "com.loicdescotte.coffeebean" % "html5tags_2.9.2" % "1.0-SNAPSHOT",
       "reactivemongo" %% "reactivemongo" % "0.1-SNAPSHOT",
       "play.modules.reactivemongo" %% "play2-reactivemongo" % "0.1-SNAPSHOT"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
     resolvers ++= Seq(
      "Local Play Repository" at "file://home/descottl/dev/play-2.0.4/repository/local",
      "sgodbillon" at "https://bitbucket.org/sgodbillon/repository/raw/master/snapshots/"
    )
   )

}

name := """respondeco"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
    jdbc,
    cache,
    ws,
    specs2 % Test,
    evolutions,
    "com.typesafe.play" %% "anorm" % "3.0.0-M1",
    "jp.t2v" %% "play2-auth"        % "0.14.1"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator


fork in run := false

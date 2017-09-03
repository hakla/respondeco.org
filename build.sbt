name := """respondeco"""

version := "0.1-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
    jdbc,
    cache,
    ws,
    specs2 % Test,
    evolutions,
    "com.typesafe.play" %% "anorm" % "3.0.0-M1",
    "jp.t2v" %% "play2-auth" % "0.14.1",
    "org.typelevel" %% "cats" % "0.8.1",
    "org.yaml" % "snakeyaml" % "1.17",
    "de.mkammerer" % "argon2-jvm" % "2.1",
    "se.digiplant" %% "play-res" % "1.2.0",

    // Image processing
    "com.sksamuel.scrimage" %% "scrimage-core" % "2.1.7",
    "com.sksamuel.scrimage" %% "scrimage-io-extra" % "2.1.7",
    "com.sksamuel.scrimage" %% "scrimage-filters" % "2.1.7"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

fork in run := false

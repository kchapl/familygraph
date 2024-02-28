scalaVersion := "2.13.1"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % "1.0.0-RC17",
  "org.neo4j.driver" % "neo4j-java-driver" % "1.7.5",
  "com.dimafeng"     %% "neotypes-zio"     % "0.13.3",
  "dev.zio"          %% "zio-config"       % "1.0.0-RC11"
)

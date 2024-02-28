scalaVersion := "2.13.13"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % "1.0.18",
  "org.neo4j.driver" % "neo4j-java-driver" % "1.7.6",
  "com.dimafeng"     %% "neotypes-zio"     % "0.13.3",
  "dev.zio"          %% "zio-config"       % "1.0.10"
)

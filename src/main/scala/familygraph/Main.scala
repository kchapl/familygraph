package familygraph

import zio.console.Console
import zio.{RIO, ZEnv, ZIO, console}

object Main extends zio.App {

  val program: RIO[Console with Graph, Unit] =
    for {
      _               <- console.putStrLn("P")
      parentName      <- console.getStrLn
      parentBirthYear <- console.getStrLn
      _               <- Graph.>.add(Person(parentName, parentBirthYear.toInt))
      parent          <- Graph.>.getByName(parentName)
      _               <- console.putStrLn(parent.toString)
      _               <- console.putStrLn("C")
      childName       <- console.getStrLn
      childBirthYear  <- console.getStrLn
      _               <- Graph.>.add(Person(childName, childBirthYear.toInt))
      child           <- Graph.>.getByName(childName)
      _               <- console.putStrLn(child.toString)
      _               <- Graph.>.addChildRelation(parent, child)
    } yield ()

  def run(args: List[String]): ZIO[ZEnv, Nothing, Int] =
    program
      .provide(new Console.Live with ConfigurationLive with GraphLive {})
      .tapError(e => console.putStrLn(e.toString))
      .fold(_ => 1, _ => 0)
}

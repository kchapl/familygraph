package familygraph

import zio._
import zio.console.Console

object Main extends App {

  val program: RIO[Console with Graph, Unit] =
    for {
      _          <- console.putStrLn("P")
      parentName <- console.getStrLn
      _          <- Graph.>.add(Person(parentName, 1969))
      parent     <- Graph.>.getByName(parentName)
      _          <- console.putStrLn(parent.toString)
      _          <- console.putStrLn("C")
      childName  <- console.getStrLn
      _          <- Graph.>.add(Person(childName, 2007))
      child      <- Graph.>.getByName(childName)
      _          <- console.putStrLn(child.toString)
      _          <- Graph.>.addChildRelation(parent, child)
    } yield ()

  def run(args: List[String]): ZIO[ZEnv, Nothing, Int] =
    for {
      out <- program
        .provide(new Console.Live with ConfigurationLive with GraphLive {})
        .tapError(e => console.putStrLn(e.toString))
        .fold(_ => 1, _ => 0)
    } yield out
}

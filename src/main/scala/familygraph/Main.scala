package familygraph

import zio._
import zio.console.Console

object Main extends App {

  val program: RIO[Console with Graph, Unit] =
    for {
      _      <- console.putStrLn("here")
      name   <- console.getStrLn
      _      <- Graph.>.add(Person(1, 1969, name))
      person <- Graph.>.getById(1)
      _      <- console.putStrLn(person.toString)
    } yield ()

  def run(args: List[String]): ZIO[ZEnv, Nothing, Int] =
    for {
      out <- program
        .provide(new Console.Live with ConfigurationLive with GraphLive {})
        .tapError(e => console.putStrLn(e.toString))
        .fold(_ => 1, _ => 0)
    } yield out
}

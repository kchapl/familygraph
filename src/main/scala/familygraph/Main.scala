package familygraph

import zio.console.Console
import zio.{RIO, ZEnv, ZIO, console}

object Main extends zio.App {

  // TODO: only add if doesn't already exist by name and birth year
  def addPerson(): RIO[Console with Graph, Unit] =
    for {
      _         <- console.putStrLn("Name:")
      name      <- console.getStrLn
      _         <- console.putStrLn("Year of birth:")
      birthYear <- console.getStrLn
      _         <- Graph.>.add(Person(name, birthYear.toInt))
      person    <- Graph.>.getByName(name)
      _         <- console.putStrLn(s"Added ${person.toString}")
    } yield ()

  val program: RIO[Console with Graph, Unit] = {
    def go(): RIO[Console with Graph, Unit] =
      for {
        _ <- console.putStrLn(
          "Option:\n1. Add person\n2. Add mother-child relation\n3. Add father-child relation\n4. Exit")
        option <- console.getStrLn
        _ <- option match {
          case "1" => addPerson() *> go()
          case "2" => console.putStrLn("Option " + option) *> go()
          case "3" => console.putStrLn("Option " + option) *> go()
          case "4" => console.putStrLn("Option " + option)
        }
      } yield ()
    go()
  }

  def run(args: List[String]): ZIO[ZEnv, Nothing, Int] =
    program
      .provide(new Console.Live with ConfigurationLive with GraphLive {})
      .tapError(e => console.putStrLn(e.toString))
      .fold(_ => 1, _ => 0)
}

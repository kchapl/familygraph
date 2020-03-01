package familygraph

import zio.console.Console
import zio.{RIO, ZEnv, ZIO, console}

object Main extends zio.App {

  private val addPerson: RIO[Console with Graph, Unit] =
    for {
      _         <- console.putStrLn("Name:")
      name      <- console.getStrLn
      _         <- console.putStrLn("Year of birth:")
      birthYear <- console.getStrLn
      _         <- Graph.>.add(Person(name, birthYear.toInt))
      person    <- Graph.>.getByName(name)
      _         <- console.putStrLn(s"Added ${person.toString}")
    } yield ()

  private def getPerson(prompt: String): RIO[Console with Graph, Person] =
    for {
      _      <- console.putStrLn(prompt)
      name   <- console.getStrLn
      person <- Graph.>.getByName(name)
    } yield person

  private val addFatherChildRelation: RIO[Console with Graph, Unit] =
    for {
      father <- getPerson("Father's name:")
      child  <- getPerson("Child's name:")
      _      <- Graph.>.addFatherChildRelation(father, child)
      _      <- console.putStrLn(s"Added father-child relation")
    } yield ()

  val program: RIO[Console with Graph, Unit] = {
    def go(): RIO[Console with Graph, Unit] =
      for {
        _ <- console.putStrLn(
          "Option:\n1. Add person\n2. Add mother-child relation\n3. Add father-child relation\n4. Exit")
        option <- console.getStrLn
        _ <- option match {
          case "1" => addPerson *> go()
          case "2" => console.putStrLn("Option " + option) *> go()
          case "3" => addFatherChildRelation *> go()
          case "4" => ZIO.succeed(())
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

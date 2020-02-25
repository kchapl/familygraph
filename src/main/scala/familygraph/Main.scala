package familygraph

import zio.console.Console
import zio.{RIO, ZEnv, ZIO, console}

object Main extends zio.App {

  def getPerson(prompt: String): RIO[Console with Graph, Person] =
    for {
      _         <- console.putStrLn(prompt)
      name      <- console.getStrLn
      birthYear <- console.getStrLn
      _         <- Graph.>.add(Person(name, birthYear.toInt))
      person    <- Graph.>.getByName(name)
      _         <- console.putStrLn(person.toString)
    } yield person

  val program: RIO[Console with Graph, Unit] =
    for {
      father <- getPerson("F")
      mother <- getPerson("M")
      child  <- getPerson("C")
      _      <- Graph.>.addChildRelation(father, child)
      _      <- Graph.>.addFatherRelation(child, father)
      _      <- Graph.>.addMotherRelation(child, mother)
    } yield ()

  def run(args: List[String]): ZIO[ZEnv, Nothing, Int] =
    program
      .provide(new Console.Live with ConfigurationLive with GraphLive {})
      .tapError(e => console.putStrLn(e.toString))
      .fold(_ => 1, _ => 0)
}

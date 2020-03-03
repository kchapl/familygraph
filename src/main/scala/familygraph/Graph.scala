package familygraph

import zio.ZIO

trait Graph {
  val graph: Graph.Service[Any]
}

object Graph {
  trait Service[R] {
    def fetchByName(name: String): ZIO[R, GraphException, Person]
    def add(p: Person): ZIO[R, GraphException, Unit]
    def addMotherChildRelation(mother: Person, child: Person): ZIO[R, GraphException, Unit]
    def addFatherChildRelation(father: Person, child: Person): ZIO[R, GraphException, Unit]
  }

  object > extends Service[Graph] {
    def fetchByName(name: String): ZIO[Graph, GraphException, Person] =
      ZIO.accessM(_.graph.fetchByName(name))
    def add(p: Person): ZIO[Graph, GraphException, Unit] = ZIO.accessM(_.graph.add(p))
    def addFatherChildRelation(father: Person, child: Person): ZIO[Graph, GraphException, Unit] =
      ZIO.accessM(_.graph.addFatherChildRelation(father, child))
    def addMotherChildRelation(child: Person, mother: Person): ZIO[Graph, GraphException, Unit] =
      ZIO.accessM(_.graph.addMotherChildRelation(mother, child))
  }
}

package familygraph

import zio.ZIO

trait Graph {
  val graph: Graph.Service[Any]
}

object Graph {
  trait Service[R] {
    def getByName(name: String): ZIO[R, GraphException, Person]
    def add(p: Person): ZIO[R, GraphException, Unit]
    def addChildRelation(parent: Person, child: Person): ZIO[R, GraphException, Unit]
    def addFatherRelation(child: Person, father: Person): ZIO[R, GraphException, Unit]
    def addMotherRelation(child: Person, mother: Person): ZIO[R, GraphException, Unit]
  }

  object > extends Service[Graph] {
    def getByName(name: String): ZIO[Graph, GraphException, Person] =
      ZIO.accessM(_.graph.getByName(name))
    def add(p: Person): ZIO[Graph, GraphException, Unit] = ZIO.accessM(_.graph.add(p))
    def addChildRelation(parent: Person, child: Person): ZIO[Graph, GraphException, Unit] =
      ZIO.accessM(_.graph.addChildRelation(parent, child))
    def addFatherRelation(child: Person, father: Person): ZIO[Graph, GraphException, Unit] =
      ZIO.accessM(_.graph.addFatherRelation(child, father))
    def addMotherRelation(child: Person, mother: Person): ZIO[Graph, GraphException, Unit] =
      ZIO.accessM(_.graph.addMotherRelation(child, mother))
  }
}

package familygraph

import zio.ZIO

trait Graph {
  val graph: Graph.Service[Any]
}

object Graph {
  trait Service[R] {
    def getById(id: Long): ZIO[R, GraphException, Person]
    def add(p: Person): ZIO[R, GraphException, Unit]
  }

  object > extends Service[Graph] {
    def getById(id: Long): ZIO[Graph, GraphException, Person] = ZIO.accessM(_.graph.getById(id))
    def add(p: Person): ZIO[Graph, GraphException, Unit]      = ZIO.accessM(_.graph.add(p))
  }
}

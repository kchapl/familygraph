package familygraph

import zio.{RIO, ZIO}

trait Graph {
  val graph: Graph.Service[Any]
}

object Graph {
  trait Service[R] {
    def getById(id: Long): RIO[R, Person]
    def add(p: Person): RIO[R, Unit]
  }

  object > extends Service[Graph] {
    def getById(id: Long): RIO[Graph, Person] = ZIO.accessM(_.graph.getById(id))
    def add(p: Person): RIO[Graph, Unit]      = ZIO.accessM(_.graph.add(p))
  }
}

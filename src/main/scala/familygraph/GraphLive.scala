package familygraph

import familygraph.Graph.Service
import neotypes.implicits.mappers.all._
import neotypes.implicits.syntax.string._
import neotypes.types.QueryParam
import neotypes.zio.implicits._
import neotypes.{GraphDatabase, Session}
import org.neo4j.driver.v1.AuthTokens
import zio._

trait GraphLive extends Graph {

  val config: Configuration.Service[Any]

  val graph: Service[Any] = new Service[Any] {

    private val session: TaskManaged[Session[Task]] =
      for {
        config   <- ZManaged.fromEffect(config.load)
        userName <- ZManaged.succeed(config.userName)
        password <- ZManaged.succeed(config.password)
        token = AuthTokens.basic(userName, password)
        driver  <- GraphDatabase.driver[Task]("bolt://localhost:7687", token)
        session <- driver.session
      } yield session

    def getById(id: Long): Task[Person] =
      session.use { s =>
        s"MATCH (p:Person) WHERE p.id = ${id.toString} RETURN p LIMIT 1".query[Person].single(s)
      }

    def add(p: Person): Task[Unit] = session.use { s =>
      "CREATE (n:Person {id: $id,  name: $name, born: $born})"
        .query[Unit]
        .withParams(
          Map("id" -> QueryParam(p.id), "name" -> QueryParam(p.name), "born" -> QueryParam(p.born)))
        .execute(s)
    }
  }
}

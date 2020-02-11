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

    private val session: Managed[SessionException, Session[Task]] =
      for {
        config   <- Managed.fromEffect(config.load).mapError(SessionException)
        userName <- Managed.succeed(config.userName)
        password <- Managed.succeed(config.password)
        token = AuthTokens.basic(userName, password)
        driver <- GraphDatabase
          .driver[Task]("bolt://localhost:7687", token)
          .mapError(SessionException)
        session <- driver.session.mapError(SessionException)
      } yield session

    def getById(id: Long): IO[GraphException, Person] =
      session
        .use { s =>
          s"MATCH (p:Person) WHERE p.id = ${id.toString} RETURN p LIMIT 1"
            .query[Person]
            .single(s)
            .mapError(NodeMatchException)
        }

    def add(p: Person): IO[GraphException, Unit] =
      session
        .use { s =>
          "CREATE (n:Person {id: $id,  name: $name, born: $born})"
            .query[Unit]
            .withParams(
              Map("id"   -> QueryParam(p.id),
                  "name" -> QueryParam(p.name),
                  "born" -> QueryParam(p.born)))
            .execute(s)
            .mapError(NodeCreateException)
        }
  }
}

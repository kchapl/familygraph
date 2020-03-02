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

    def getByName(name: String): IO[GraphException, Person] =
      session.use { s =>
        Query.getByName
          .query[Person]
          .withParams(Map("name" -> QueryParam(name)))
          .single(s)
          .mapError(NodeMatchException)
      }

    private def create(q: String, params: Map[String, QueryParam]): IO[GraphException, Unit] =
      session.use { s =>
        q.query[Unit]
          .withParams(params)
          .execute(s)
          .mapError(NodeCreateException)
      }

    def add(p: Person): IO[GraphException, Unit] =
      create(
        Query.createPerson,
        Map("name" -> QueryParam(p.name), "born" -> QueryParam(p.born))
      )

    private def addChildRelation(parent: Person, child: Person): IO[GraphException, Unit] =
      create(
        Query.createChildRelation,
        Map(
          "parentName" -> QueryParam(parent.name),
          "childName"  -> QueryParam(child.name)
        )
      )

    private def addMotherRelation(child: Person, mother: Person): IO[GraphException, Unit] =
      create(
        Query.createMotherRelation,
        Map(
          "childName"  -> QueryParam(child.name),
          "motherName" -> QueryParam(mother.name)
        )
      )

    private def addFatherRelation(child: Person, father: Person): IO[GraphException, Unit] =
      create(
        Query.createFatherRelation,
        Map(
          "childName"  -> QueryParam(child.name),
          "fatherName" -> QueryParam(father.name)
        )
      )

    def addMotherChildRelation(mother: Person, child: Person): ZIO[Any, GraphException, Unit] =
      for {
        _ <- addChildRelation(mother, child)
        _ <- addMotherRelation(child, mother)
      } yield ()

    def addFatherChildRelation(father: Person, child: Person): ZIO[Any, GraphException, Unit] =
      for {
        _ <- addChildRelation(father, child)
        _ <- addFatherRelation(child, father)
      } yield ()
  }

  object Query {

    val getByName: String =
      """MATCH (p:Person { name: $name })
        |RETURN p""".stripMargin

    val createPerson: String = "CREATE (n:Person { name: $name, born: $born })"

    val createChildRelation: String =
      """MATCH (parent:Person { name: $parentName }), (child:Person { name: $childName })
        |CREATE (child)-[r:CHILD_OF]->(parent)""".stripMargin

    val createFatherRelation: String =
      """MATCH (child:Person { name: $childName }), (father:Person { name: $fatherName })
        |CREATE (father)-[r:FATHER_OF]->(child)""".stripMargin

    val createMotherRelation: String =
      """MATCH (child:Person { name: $childName }), (mother:Person { name: $motherName })
        |CREATE (mother)-[r:MOTHER_OF]->(child)""".stripMargin
  }
}

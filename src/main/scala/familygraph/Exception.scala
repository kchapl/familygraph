package familygraph

abstract class GraphException(cause: Throwable) extends RuntimeException(cause)

case class SessionException(cause: Throwable)    extends GraphException(cause)
case class NodeCreateException(cause: Throwable) extends GraphException(cause)
case class NodeMatchException(cause: Throwable)  extends GraphException(cause)

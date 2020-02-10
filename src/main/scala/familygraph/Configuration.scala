package familygraph

import zio.RIO

trait Configuration {
  val config: Configuration.Service[Any]
}

object Configuration {
  trait Service[R] {
    val load: RIO[R, Config]
  }

  object > extends Service[Configuration] {
    val load: RIO[Configuration, Config] = RIO.accessM(_.config.load)
  }
}

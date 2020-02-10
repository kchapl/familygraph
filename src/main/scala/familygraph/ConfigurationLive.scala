package familygraph

import zio.Task
import zio.config.ConfigDescriptor.string
import zio.config.{ConfigSource, read}

trait ConfigurationLive extends Configuration {
  val config: Configuration.Service[Any] = new Configuration.Service[Any] {
    val load: Task[Config] = {
      val config = (string("USR") |@| string("PWD"))(Config.apply, Config.unapply)
      read(config from ConfigSource.fromEnv()).mapError(e => new RuntimeException(e.toString))
    }
  }
}

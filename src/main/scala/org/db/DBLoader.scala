package org.db

import com.typesafe.config.ConfigFactory
import org.service.PersistenceService
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.{JdbcProfile, PostgresProfile}

object DBLoader {
  def load(profileName: String): PersistenceService = {

    val env = sys.props.getOrElse("APP_ENV", "local") // default to local

    val config = ConfigFactory.load(s"application-$env.conf")
    val rootConfig = ConfigFactory.load(s"application.conf")

    val (configProfile, dbConfigPath) = profileName match {
      case "postgres" => (PostgresProfile, "db.postgres")
      case _          => throw new Exception("Unsupported DB profile")
    }

    new PersistenceService {
      override val profile: JdbcProfile = configProfile
      override val db: Database = Database.forConfig(s"$dbConfigPath.db",config)
      override val bucket: String = rootConfig.getString("general.bucket_name")
    }
  }
}

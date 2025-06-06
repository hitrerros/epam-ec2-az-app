package org.service.configuration

import com.typesafe.config.{Config, ConfigFactory}
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.{JdbcProfile, PostgresProfile}

trait ConfigurationService {
  val env: String = sys.props.getOrElse("APP_ENV", "local") // default to local

  val config: Config = ConfigFactory.load(s"application-$env.conf")
  val rootConfig: Config = ConfigFactory.load(s"application.conf")

  val bucket: String = rootConfig.getString("general.bucket_name")
  val email : String = rootConfig.getString("general.email")
  val emailPassword : String = rootConfig.getString("general.email_password")
}

case class DBConfigurationService(profileName : String) extends ConfigurationService {
  private val (configProfile, dbConfigPath) = profileName match {
    case "postgres" => (PostgresProfile, "db.postgres")
    case _          => throw new Exception("Unsupported DB profile")
  }
  val profile: JdbcProfile = configProfile
  val db: Database = Database.forConfig(s"$dbConfigPath.db", config)
}

object ConfigurationService extends ConfigurationService
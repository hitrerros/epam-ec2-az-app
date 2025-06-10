package org.service.configuration

import com.typesafe.config.{Config, ConfigFactory}
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.{JdbcProfile, PostgresProfile}

trait ConfigurationService {
  private val rootConfig: Config = ConfigFactory.load(s"application.conf")

  val env: String = sys.props.getOrElse("APP_ENV", "local") // default to local
  val bucket: String = rootConfig.getString("general.bucket_name")
  val appName: String = rootConfig.getString("general.app_name")
  val email : String = rootConfig.getString("general.email")
  val emailPassword : String = rootConfig.getString("general.email_password")
  val queueUrl : String = rootConfig.getString("general.queue_url")
  val snsArn : String = rootConfig.getString("general.sns_arn")
  val cronExpr : String = rootConfig.getString("general.sns_cron")
}

case class DBConfigurationService(profileName : String) extends ConfigurationService {
  private val config: Config = ConfigFactory.load(s"application-$env.conf")

  private val (configProfile, dbConfigPath) = profileName match {
    case "postgres" => (PostgresProfile, "db.postgres")
    case _          => throw new Exception("Unsupported DB profile")
  }
  val profile: JdbcProfile = configProfile
  val db: Database = Database.forConfig(s"$dbConfigPath.db", config)
}

object ConfigurationService extends ConfigurationService
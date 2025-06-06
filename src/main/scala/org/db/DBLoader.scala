package org.db

import org.service.PersistenceService
import org.service.configuration.DBConfigurationService
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.JdbcProfile

object DBLoader {
  def load(profileName: String): PersistenceService = {
    val dbConfigService = DBConfigurationService(profileName)
    new PersistenceService {
      override val profile: JdbcProfile = dbConfigService.profile
      override val db: Database = dbConfigService.db
    }
  }
}

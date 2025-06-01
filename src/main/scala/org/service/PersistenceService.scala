package org.service

import org.db.DBLoader
import org.db.model.{MetadataRecord, MetadataTable}
import org.service.PersistenceService.dbPostgresService
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.JdbcProfile

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


trait PersistenceService {
  val profile: JdbcProfile
  val db: Database
  val bucket: String

  import dbPostgresService.profile.api._
  private val metadataTab = TableQuery[MetadataTable]

  def uploadMetadatInfoToDB(
      filename: String,
      content: Array[Byte]
  ): Future[Option[MetadataRecord]] = {
    val entity = MetadataRecord(
      1,
      filename,
      filename.split('.') match {
        case x if x.length == 2 => x.tail.head
        case _                  => filename
      },
      content.length.toLong,
      LocalDateTime.now
    )


    val setup = DBIO.seq(metadataTab.schema.createIfNotExists, metadataTab += entity)
    db.run(setup).map(_ => Some(entity)).recover { case _ => None }
  }

  def deleteFile(filename : String) : Future[Int] = {
    db.run(metadataTab.filter(_.fileName===filename).delete)
  }

  def showMetadata(filename: Option[String]): Future[Option[MetadataRecord]] = {
    filename match {
      case Some(v) => db.run(metadataTab.filter(_.fileName === filename).result.headOption)
      case None =>
        for {
          id <- db.run(metadataTab.map(_.id).result.head)
          lines <- db.run(metadataTab.filter(_.id === id).result.headOption)
        } yield lines
    }
  }

}



object PersistenceService {
  implicit val dbPostgresService: PersistenceService = DBLoader.load("postgres")
}

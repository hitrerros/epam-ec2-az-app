package org.db.model

import slick.jdbc.PostgresProfile.api._

import java.time.LocalDateTime

// last update date, name, size(in bytes), and file extension
case class MetadataRecord(
    id: Int,
    fileName: String,
    extension: String,
    size: Long,
    lastUpdate: LocalDateTime
)

class MetadataTable(tag: Tag)
    extends Table[MetadataRecord](tag, "file_metadata") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def fileName = column[String]("fileName")
  def extension = column[String]("extension")
  def size = column[Long]("size")
  def lastUpdate = column[LocalDateTime]("lastUpdate")

  def * = (
    id,
    fileName,
    extension,
    size,
    lastUpdate
  ) <> (MetadataRecord.tupled, MetadataRecord.unapply)
}


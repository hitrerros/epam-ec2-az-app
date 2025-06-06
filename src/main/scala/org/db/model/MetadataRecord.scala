package org.db.model

import play.api.libs.json.{Json, Writes}

import java.time.LocalDateTime

// last update date, name, size(in bytes), and file extension
case class MetadataRecord(
    id: Int,
    fileName: String,
    extension: String,
    size: Long,
    lastUpdate: LocalDateTime
)

object MetadataRecord {
  implicit val writes: Writes[MetadataRecord] = Json.writes[MetadataRecord]
}
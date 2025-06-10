package org.db.model

import play.api.libs.functional.syntax._
import play.api.libs.json._

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

case class MetadataRecord(
                           id: Int,
                           fileName: String,
                           extension: String,
                           size: Long,
                           lastUpdate: LocalDateTime
                         )

object MetadataRecord {

  private val formatString = "yyyy-MM-dd'T'HH:mm:ss"
  private val dateTimeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern(formatString)

  implicit val localDateTimeReader: Reads[LocalDateTime] =
    Reads.localDateTimeReads(formatString)

  implicit val localDateTimeWriter: Writes[LocalDateTime] =
    Writes[LocalDateTime](dt => JsString(dt.format(dateTimeFormat)))

  implicit val writer: Writes[MetadataRecord] = Json.writes[MetadataRecord]

  implicit val reader: Reads[MetadataRecord] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "fileName").read[String] and
      (JsPath \ "extension").read[String] and
      (JsPath \ "size").read[Long] and
      (JsPath \ "lastUpdate").read[LocalDateTime]
    )(MetadataRecord.apply _)

}

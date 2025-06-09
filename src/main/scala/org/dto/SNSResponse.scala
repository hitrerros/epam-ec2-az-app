package org.dto

import org.db.model.MetadataRecord
import play.api.libs.json.{Json, Writes}

case class SNSResponse(
    message: String,
    metadataInfo: MetadataRecord,
    link: String
)

object SNSResponse {
  implicit val writer: Writes[SNSResponse] = Json.writes[SNSResponse]
}

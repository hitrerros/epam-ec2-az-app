package org.service.configuration

import org.db.model.MetadataRecord
import play.api.libs.json.Json

import scala.language.implicitConversions

object ServiceImplicits {
   implicit def jsonToSting(metadataRecord : MetadataRecord) : String = {
       Json.toJson(metadataRecord).toString()
   }
}

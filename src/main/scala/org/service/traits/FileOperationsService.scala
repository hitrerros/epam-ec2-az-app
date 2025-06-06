package org.service

import cats.effect.IO
import org.db.model.MetadataRecord
import org.dto.AvailabilityZoneResponse

trait FileOperationsService {
  def getAvailabilityZoneAndRegion: IO[Option[AvailabilityZoneResponse]]
  def uploadFile(filename : String, content : Array[Byte]): IO[Option[MetadataRecord]]
  def downloadFile(filename : String): IO[Option[Array[Byte]]]
  def deleteFile(filename : String): IO[Boolean]
  def showMetadata(filename : Option[String]): IO[Option[MetadataRecord]]
}

object FileOperationsService {
  implicit val sdkService: FileOperationsService = new LiveFileOperationsService()
   val dbService: PersistenceService = implicitly[PersistenceService]
}

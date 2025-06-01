package org.service

import cats.effect.IO
import org.db.model.MetadataRecord
import org.dto.AvailabilityZoneResponse

trait AmazonSdkService {
  def getAvailabilityZoneAndRegion: IO[Option[AvailabilityZoneResponse]]
  def uploadFile(filename : String, content : Array[Byte]): IO[Option[MetadataRecord]]
  def downloadFile(filename : String): IO[Option[Array[Byte]]]
  def deleteFile(filename : String): IO[Boolean]
  def showMetadata(filename : Option[String]): IO[Option[MetadataRecord]]
}

object AmazonSdkService {
  implicit val sdkService: AmazonSdkService = new LiveAmazonSdkService()
}

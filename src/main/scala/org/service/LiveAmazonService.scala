package org.service

import cats.effect.IO
import software.amazon.awssdk.regions.internal.util.EC2MetadataUtils

import scala.util.{Success, Try}

class LiveAmazonService extends AmazonService {
  override def getAvailabilityZoneAndRegion: IO[Option[AmazonResponse]] = {
    Try(EC2MetadataUtils.getAvailabilityZone) match {
      case Success(value) =>
        IO.pure(Some(AmazonResponse(value, value.dropRight(1))))
      case _ => IO.none
    }
  }
}

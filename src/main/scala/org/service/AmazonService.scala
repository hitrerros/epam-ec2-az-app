package org.service

import cats.effect.IO

trait AmazonService {
  def getAvailabilityZoneAndRegion:  IO[Option[AmazonResponse]]
}

object AmazonService {
  implicit val userService: AmazonService = new LiveAmazonService()
}
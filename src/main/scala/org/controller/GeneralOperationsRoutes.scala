package org.controller

import cats.effect.IO
import org.http4s.HttpRoutes
import org.http4s.dsl.io._

object GeneralOperationsRoutes {
  import RoutesImplicits._

  def routes: HttpRoutes[IO] = {
    HttpRoutes.of[IO] {
      case GET -> Root =>
        Ok("Hey there!")

      case GET -> Root / "az" =>
        amazonSdkService.getAvailabilityZoneAndRegion flatMap {
          case Some(v) => Ok(v)
          case None    => InternalServerError(s"not found")
        }
    }
  }
}

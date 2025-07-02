package org.controller

import cats.effect.IO
import org.http4s.dsl.io._
import org.http4s.headers.`Content-Type`
import org.http4s.{HttpRoutes, MediaType}
import org.service.FrontendService

object GeneralOperationsRoutes {
  import RoutesImplicits._

  def routes: HttpRoutes[IO] = {
    HttpRoutes.of[IO] {
      case GET -> Root =>
        Ok(FrontendService.getIndexTemplateHtml)
          .map(_.withContentType(`Content-Type`(MediaType.text.html)))
      case GET -> Root / "az" =>
        amazonSdkService.getAvailabilityZoneAndRegion flatMap {
          case Some(v) => Ok(v)
          case None    => InternalServerError(s"not found")
        }

    }
  }

}

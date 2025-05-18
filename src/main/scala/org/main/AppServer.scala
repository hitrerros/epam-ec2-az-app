package org.main

import cats.effect.{ExitCode, IO, IOApp}
import org.http4s.HttpRoutes
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.dsl.io._
import org.service.AmazonService

object Routes {
  def routes: HttpRoutes[IO] = {
    val service = implicitly[AmazonService]
    HttpRoutes.of[IO] {
      case GET -> Root / "az" =>
        service.getAvailabilityZoneAndRegion flatMap {
          case Some(v) => Ok(v.region + "," + v.az)
          case None    => InternalServerError(s"not found")
        }
      case GET -> Root =>
        Ok("Hey there!")
    }
  }
}

object AppServer extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    BlazeServerBuilder[IO]
      .bindHttp(80, "0.0.0.0")
      .withHttpApp(Routes.routes.orNotFound)
      .resource
      .useForever
      .as(ExitCode.Success)
  }
}

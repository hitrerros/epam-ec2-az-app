package org.main

import cats.effect.{ExitCode, IO, IOApp}
import org.controller.{FileMetadataRoutes, FileOperationsRoutes, GeneralOperationsRoutes, SQSOperationsRoutes}
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Router

object AppServer extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {

    val httpAppList = Router(
      "/" -> GeneralOperationsRoutes.routes,
      "/files" -> FileOperationsRoutes.routes,
      "/info" -> FileMetadataRoutes.routes,
      "/queues" -> SQSOperationsRoutes.routes,
    )

    BlazeServerBuilder[IO]
      .bindHttp(80, "0.0.0.0")
      .withHttpApp(httpAppList.orNotFound)
      .resource
      .useForever
      .as(ExitCode.Success)
  }
}

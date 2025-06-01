package org.main

import cats.effect.{ExitCode, IO, IOApp}
import org.controller.Routes

import org.http4s.blaze.server.BlazeServerBuilder


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

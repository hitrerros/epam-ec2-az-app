package org.controller

import cats.effect.IO
import org.http4s.HttpRoutes
import org.http4s.dsl.io._
import org.service.internal.LambdaInvocationService

object LambdaInvocationRoutes {
  def routes: HttpRoutes[IO] = {
    HttpRoutes.of[IO] {
      case GET -> Root / lambdaName =>
      for {
        response <- LambdaInvocationService.callLambda(lambdaName)
        resp <- Ok(s"response $response")
      } yield resp
    }
  }
}

package org.controller

import cats.effect.IO
import org.http4s.HttpRoutes
import org.http4s.dsl.io._

object SQSOperationsRoutes {
  import RoutesImplicits._

  def routes: HttpRoutes[IO] = {
    HttpRoutes.of[IO] {
      case GET -> Root / "subscribe" :? ObligatoryMailParam(mail) =>
        sqsOperationsRoutes.subscribe(mail) flatMap (k =>
          Ok(s"subscribed ${k.toString}!")
        )
      case GET -> Root / "unsubscribe" :? ObligatoryMailParam(mail) =>
        sqsOperationsRoutes.unsubscribe(mail) flatMap {
          case Some(k) =>  Ok(s"unsubscribed ${k.toString}!")
          case None => InternalServerError("not unsubscribed")
        }

    }
  }
}

object ObligatoryMailParam extends QueryParamDecoderMatcher[String]("mail")

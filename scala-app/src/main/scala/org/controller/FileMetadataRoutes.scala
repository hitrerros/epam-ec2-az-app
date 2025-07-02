package org.controller

import cats.effect.IO
import org.http4s.HttpRoutes
import org.http4s.dsl.io._

object FileMetadataRoutes {
  import RoutesImplicits._

  def routes: HttpRoutes[IO] = {
    HttpRoutes.of[IO] { case GET -> Root :? OptionalFilenameParam(filename) =>
      amazonSdkService.showMetadata(filename) flatMap {
        case Some(metadata) => Ok(metadata)
        case _              => InternalServerError("file not found")
      }
    }
  }
}

object OptionalFilenameParam
    extends OptionalQueryParamDecoderMatcher[String]("filename")

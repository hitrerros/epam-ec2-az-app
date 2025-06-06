package org.controller

import cats.effect.IO
import org.http4s.HttpRoutes
import org.http4s.dsl.io._

object FileOperationsRoutes {
  import RoutesImplicits._

  def routes: HttpRoutes[IO] = {
    HttpRoutes.of[IO] {

      case req @ POST -> Root /  filename =>
        val s: IO[Array[Byte]] = req.body.compile.to(Array)

        s flatMap (fileContent => {
          amazonSdkservice
            .uploadFile(filename, fileContent)
            .flatMap {
              case Some(metadata) => Ok(metadata)
              case None    => InternalServerError("not found")
            }
        })

      case GET -> Root / filename =>
        amazonSdkservice.downloadFile(filename) flatMap {
          case Some(content) => Ok(content)
          case None => InternalServerError("not content")
        }

      case DELETE -> Root /  filename =>
        amazonSdkservice.deleteFile(filename) flatMap {
          case true => Ok( s"${filename} deleted")
          case _ => InternalServerError("not content")
        }

    }
  }
}



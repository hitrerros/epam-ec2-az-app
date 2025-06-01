package org.controller

import cats.effect.IO
import org.http4s.HttpRoutes
import org.http4s.dsl.io._
import org.service.AmazonSdkService

object Routes {
  private val amazonSdkservice = implicitly[AmazonSdkService]

  def routes: HttpRoutes[IO] = {
    HttpRoutes.of[IO] {
      case GET -> Root =>
        Ok("Hey there!")

      case GET -> Root / "az" =>
        amazonSdkservice.getAvailabilityZoneAndRegion flatMap {
          case Some(v) => Ok(v.region + "," + v.az)
          case None    => InternalServerError(s"not found")
        }

      case req @ POST -> Root / "files" / filename =>
        val s: IO[Array[Byte]] = req.body.compile.to(Array)
        s flatMap (fileContent => {
          amazonSdkservice
            .uploadFile(filename, fileContent)
            .flatMap {
              case Some(v) => Ok(v.toString)
              case None    => InternalServerError("not found")
            }
        })

      case GET -> Root / "files" / filename =>
        amazonSdkservice.downloadFile(filename) flatMap {
          case Some(content) => Ok(content)
          case None => InternalServerError("not content")
        }

      case DELETE -> Root / "files" / filename =>
        amazonSdkservice.deleteFile(filename) flatMap {
          case true => Ok("done")
          case _ => InternalServerError("not content")
        }

      case GET -> Root / "info" :? FilenameParam(filename) =>
        amazonSdkservice.showMetadata(filename) flatMap {
          case Some(v) => Ok(v.toString)
          case _ => InternalServerError("not content")
        }
    }
  }
}

object FilenameParam extends OptionalQueryParamDecoderMatcher[String]("filename")


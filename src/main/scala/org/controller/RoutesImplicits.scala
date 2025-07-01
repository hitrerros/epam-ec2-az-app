package org.controller

import cats.effect.IO
import org.db.model.MetadataRecord
import org.dto.AvailabilityZoneResponse
import org.http4s.headers.`Content-Type`
import org.http4s.{EntityEncoder, MediaType}
import org.service.FileOperationsService
import org.service.traits.SNSOperationsService
import play.api.libs.json.Json

import scala.language.implicitConversions

object RoutesImplicits {

  implicit val userEncoder: EntityEncoder[IO, MetadataRecord] =
    EntityEncoder
      .stringEncoder[IO]
      .contramap[MetadataRecord](u => Json.stringify(Json.toJson(u)))
      .withContentType(`Content-Type`(MediaType.application.json))

  implicit val availabilityZoneResponseEncoder
      : EntityEncoder[IO, AvailabilityZoneResponse] =
    EntityEncoder
      .stringEncoder[IO]
      .contramap[AvailabilityZoneResponse](response =>
        s"Zone = ${response.az} , region = ${response.region} "
      )

  implicit val stringEncoder: EntityEncoder[IO, String] =
    EntityEncoder.stringEncoder[IO]

  implicit val byteArrayEncoder: EntityEncoder[IO, Array[Byte]] =
    EntityEncoder.byteArrayEncoder[IO]

  implicit def fallbackToStringEncoder[A]: EntityEncoder[IO, A] =
    EntityEncoder
      .stringEncoder[IO]
      .contramap[A](_.toString)
      .withContentType(`Content-Type`(MediaType.text.plain))

  val amazonSdkService: FileOperationsService =
    implicitly[FileOperationsService]

  val sqsOperationsRoutes: SNSOperationsService =
    implicitly[SNSOperationsService]

}

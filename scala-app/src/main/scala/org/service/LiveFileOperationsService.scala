package org.service

import cats.effect.IO
import cats.syntax.parallel._

import org.db.model.MetadataRecord
import org.dto.AvailabilityZoneResponse
import org.service.LiveFileOperationsService.{bucket, s3Client}
import org.service.configuration.ConfigurationService
import org.service.internal.{DynamoDBService, SQSSendService}
import org.service.traits.FileOperationsService
import org.service.traits.FileOperationsService.dbService
import software.amazon.awssdk.core.async.{AsyncRequestBody, AsyncResponseTransformer}
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.regions.internal.util.EC2MetadataUtils
import software.amazon.awssdk.services.dynamodb.model.UpdateItemResponse
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.{DeleteObjectRequest, GetObjectRequest, GetObjectResponse, PutObjectRequest}

import java.nio.ByteBuffer
import scala.util.{Success, Try}

class LiveFileOperationsService extends FileOperationsService {

  override def getAvailabilityZoneAndRegion
      : IO[Option[AvailabilityZoneResponse]] = {
    Try(EC2MetadataUtils.getAvailabilityZone) match {
      case Success(value) =>
        IO.pure(Some(AvailabilityZoneResponse(value, value.dropRight(1))))
      case _ => IO.none
    }
  }

  override def uploadFile(
                           filename: String,
                           content: Array[Byte]
                         ): IO[Option[MetadataRecord]] = {
    val request =
      PutObjectRequest
        .builder()
        .bucket(bucket)
        .key(filename)
        .contentType("application/octet-stream")
        .contentLength(content.length)
        .build()

    import org.service.configuration.ServiceImplicits._

    for {
      _ <- IO.fromCompletableFuture(
        IO(s3Client.putObject(request, AsyncRequestBody.fromBytes(content)))
      )

      retMetadata <- IO.fromFuture(
        IO(dbService.uploadMetadataInfoToDB(filename, content)))

      _ <- IO.fromCompletableFuture(IO(SQSSendService.sendMessage(retMetadata.get)))
      } yield (retMetadata)
     }


  override def downloadFile(filename: String): IO[Option[Array[Byte]]] = {
    val request = GetObjectRequest.builder().bucket(bucket).key(filename).build()
    val futureResponse = s3Client.getObject(request, AsyncResponseTransformer.toBytes[GetObjectResponse]())

    val statisticsEffect : IO[UpdateItemResponse] =  DynamoDBService.updateCount(filename,"download_count")

    val downloadEffect = IO.fromCompletableFuture(IO(futureResponse)).map { response =>
      val byteBuffer: ByteBuffer = response.asByteBuffer()
      val bytes = new Array[Byte](byteBuffer.remaining())
      byteBuffer.get(bytes)
      Some(bytes)
    }.recover { case _ => None }

    (statisticsEffect,downloadEffect).parTupled.map{case (_,a) => a}
  }

  override def deleteFile(filename: String): IO[Boolean] = {
    val request = DeleteObjectRequest.builder().bucket(bucket).key(filename).build()

    for {
      lines <- IO.fromFuture(
        IO(dbService.deleteFile(filename))
      )
      _ <- if (lines>0) IO.fromCompletableFuture(
        IO(s3Client.deleteObject(request))) else IO.raiseError(new Exception(""))

    } yield lines > 0
  }

  override def showMetadata(filename: Option[String]): IO[Option[MetadataRecord]] = {
    (DynamoDBService.updateCount(filename.get, "view_count"),
      IO.fromFuture(IO(dbService.showMetadata(filename))))
      .parTupled.map { case (_, a) => a }
  }
}

object LiveFileOperationsService {
  val s3Client: S3AsyncClient = S3AsyncClient
    .builder()
    .httpClient(NettyNioAsyncHttpClient.builder().build())
    .region(Region.US_WEST_2)
    .build()

  val bucket: String = ConfigurationService.bucket
}

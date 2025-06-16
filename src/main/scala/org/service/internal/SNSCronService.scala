package org.service.internal

import cats.effect._
import cats.syntax.all._
import cron4s.Cron
import eu.timepit.fs2cron.cron4s.Cron4sScheduler
import fs2.Stream
import org.db.model.MetadataRecord
import org.service.configuration.ConfigurationService
import org.typelevel.log4cats.slf4j.Slf4jLogger
import play.api.libs.json.{JsError, JsSuccess, Json}
import software.amazon.awssdk.services.sns.SnsAsyncClient
import software.amazon.awssdk.services.sns.model.PublishRequest
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.{DeleteMessageRequest, ReceiveMessageRequest}

import java.time.ZonedDateTime
import scala.jdk.CollectionConverters._

object SNSCronService {

  private val queueUrl: String = ConfigurationService.queueUrl
  private val topicArn: String = ConfigurationService.snsArn
  private val batchSize: Int = 3
  private val cronExpr: String = ConfigurationService.cronExpr

  private def convertSQSMessageToString(metadataString: String): String = {
    Json.parse(metadataString).validate[MetadataRecord] match {
      case JsSuccess(record, _) =>
        s"Dear user!\n " +
          s"Image ${record.fileName} has been uploaded\n" +
          s"size ${record.size}, extension ${record.extension}\n" +
          s"download link: ${AppInfo.getAppUrl}files\\${record.fileName}"
      case JsError(_) => s"Error during image upload ${metadataString}"
    }
  }

  def processMessages(
      sqsClient: SqsAsyncClient,
      snsClient: SnsAsyncClient
  ): IO[Unit] = {
    for {
      logger <- Slf4jLogger.create[IO]
      _ <- logger.info(s"Running scheduled job at ${ZonedDateTime.now()}")

      receiveRequest = ReceiveMessageRequest
        .builder()
        .queueUrl(queueUrl)
        .maxNumberOfMessages(batchSize)
        .waitTimeSeconds(5)
        .build()

      messages <- IO
        .fromCompletableFuture(IO(sqsClient.receiveMessage(receiveRequest)))
        .map(_.messages().asScala.toList)

      _ <- logger.info(s"Fetched ${messages.size} messages from SQS - n")

      _ <- messages.traverse_ { msg =>
        val publishReq = PublishRequest
          .builder()
          .topicArn(topicArn)
          .message(convertSQSMessageToString(msg.body()))
          .build()

        val publishIO =
          IO.fromCompletableFuture(IO(snsClient.publish(publishReq)))

        val deleteIO = IO.fromCompletableFuture(IO {
          sqsClient.deleteMessage(
            DeleteMessageRequest
              .builder()
              .queueUrl(queueUrl)
              .receiptHandle(msg.receiptHandle())
              .build()
          )
        })

        for {
          _ <- publishIO
          _ <- deleteIO
          _ <- logger.info(s"Published and deleted message ${msg.messageId()}")
        } yield ()
      }
    } yield ()
  }

  def cronStream(sqs: SqsAsyncClient, sns: SnsAsyncClient): Stream[IO, Unit] = {
    val evenSeconds = Cron.unsafeParse(cronExpr)

    Cron4sScheduler
      .systemDefault[IO]
      .awakeEvery(evenSeconds)
      .evalMap(_ =>
        if (ConfigurationService.cronEnabled)
          processMessages(sqs, sns)
        else IO.unit
      )
  }

}

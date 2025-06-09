package org.service.internal

import fs2.Stream
import cats.effect._
import cats.syntax.all._
import org.service.configuration.ConfigurationService
import org.typelevel.log4cats.slf4j.Slf4jLogger
import software.amazon.awssdk.services.sns.SnsClient
import software.amazon.awssdk.services.sns.model.PublishRequest
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.{DeleteMessageRequest, ReceiveMessageRequest}
import cron4s._
import cron4s.expr.CronExpr
import cron4s.lib.javatime._

import java.time.ZonedDateTime
import scala.jdk.CollectionConverters.IterableHasAsScala

object SNSCronService {
  private val queueUrl: String = ConfigurationService.queueUrl
  private val topicArn: String = ConfigurationService.snsArn
  private val batchSize : Int = 3

  def processMessages(sqsClient: SqsClient, snsClient: SnsClient): IO[Unit] = {
    for {
      logger <- Slf4jLogger.create[IO]

      _ <- logger.info(s"Running scheduled job at ${ZonedDateTime.now()}")

      receiveRequest = ReceiveMessageRequest.builder()
        .queueUrl(queueUrl)
        .maxNumberOfMessages(batchSize)
        .waitTimeSeconds(5)
        .build()

      messages <- IO.blocking(sqsClient.receiveMessage(receiveRequest).messages()).map(_.asScala.toList)

      _ <- logger.info(s"Fetched ${messages.size} messages from SQS")

      _ <- messages.traverse_ { msg =>
        val publishReq = PublishRequest.builder()
          .topicArn(topicArn)
          .message(msg.body())
          .build()

        for {
          _ <- IO.blocking(snsClient.publish(publishReq))
          _ <- IO.blocking {
            sqsClient.deleteMessage(
              DeleteMessageRequest.builder().queueUrl(queueUrl).receiptHandle(msg.receiptHandle()).build()
            )
          }
          _ <- logger.info(s"Published and deleted message ${msg.messageId()}")
        } yield ()
      }
    } yield ()
  }

  def cronStream(sqsClient: SqsClient, snsClient: SnsClient): Stream[IO, Unit] =
    CronStream[IO]
      .awakeEvery(ConfigurationService)
      .evalMap(_ => processMessages(sqsClient, snsClient))


}

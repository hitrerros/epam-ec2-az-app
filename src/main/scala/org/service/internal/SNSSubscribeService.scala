package org.service.internal

import cats.effect.IO
import org.service.configuration.ConfigurationService
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sns.SnsAsyncClient
import software.amazon.awssdk.services.sns.model._

import scala.jdk.CollectionConverters.IterableHasAsScala

object SNSSubscribeService {

  private val snsClient: SnsAsyncClient = SnsAsyncClient
    .builder()
    .region(Region.US_WEST_2) // adjust as needed
    .build()

  def subscribeEmail(
      email: String
  ): IO[SubscribeResponse] = {
    val request = SubscribeRequest
      .builder()
      .topicArn(ConfigurationService.snsArn)
      .protocol("email")
      .endpoint(email)
      .returnSubscriptionArn(
        true
      )
      .build()

    IO.fromCompletableFuture(IO(snsClient.subscribe(request)))
  }

  def findSubscriptionArnForEmail(
      email: String
  ): IO[Option[String]] = {
    val request = ListSubscriptionsByTopicRequest
      .builder()
      .topicArn(ConfigurationService.snsArn)
      .build()

    IO.fromCompletableFuture(IO(snsClient.listSubscriptionsByTopic(request)))
      .map(
        _.subscriptions().asScala
          .find(_.endpoint() == email)
          .map(_.subscriptionArn())
      )
  }

  def unsubscribeEmail(
      email: String
  ): IO[UnsubscribeResponse] = {

    findSubscriptionArnForEmail(email).flatMap {
      case Some(arn) =>
      val request = UnsubscribeRequest
        .builder()
        .subscriptionArn(arn)
        .build()
      IO.fromCompletableFuture(IO(snsClient.unsubscribe(request)))
    }
  }

}

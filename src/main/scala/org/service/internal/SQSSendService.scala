package org.service.internal

import org.service.configuration.ConfigurationService
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.{SendMessageRequest, SendMessageResponse}

import java.util.concurrent.CompletableFuture

object SQSSendService {

  val sqsAsyncClient: SqsAsyncClient = SqsAsyncClient
    .builder()
    .region(Region.US_WEST_2)
    .build()

  def sendMessage(messageBody: String): CompletableFuture[SendMessageResponse] = {
    val request = SendMessageRequest
      .builder()
      .queueUrl(ConfigurationService.queueUrl)
      .messageBody(messageBody)
      .build()

    sqsAsyncClient.sendMessage(request)
  }
}

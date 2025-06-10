package org.service.traits

import cats.effect.IO
import org.service.LiveSQSOperationService
import software.amazon.awssdk.services.sns.model.{SubscribeResponse, UnsubscribeResponse}

trait SQSOperationsService {
  def subscribe(mail : String): IO[SubscribeResponse]
  def unsubscribe(mail : String): IO[UnsubscribeResponse]
}

object SQSOperationsService {
  implicit val SQSOperationsService: SQSOperationsService = new LiveSQSOperationService()
}

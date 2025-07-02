package org.service.traits

import cats.effect.IO
import org.service.LiveSNSOperationService
import software.amazon.awssdk.services.sns.model.{SubscribeResponse, UnsubscribeResponse}

trait SNSOperationsService {
  def subscribe(mail : String): IO[SubscribeResponse]
  def unsubscribe(mail : String): IO[Option[UnsubscribeResponse]]
}

object SNSOperationsService {
  implicit val SNSOperationsService: SNSOperationsService = new LiveSNSOperationService()
}

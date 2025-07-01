package org.service

import cats.effect.IO
import org.service.FileOperationsService.dbService
import org.service.internal.SNSSubscribeService
import org.service.traits.SNSOperationsService
import software.amazon.awssdk.services.sns.model.{SubscribeResponse, UnsubscribeResponse}

class LiveSNSOperationService extends SNSOperationsService {

  override def subscribe(mail: String): IO[SubscribeResponse] = {
    for {
      response <- SNSSubscribeService.subscribeEmail(mail)
      _ <- IO.fromFuture(IO(dbService.subscribeAtMailingList(mail))) //
    } yield response
  }

  override def unsubscribe(mail: String): IO[Option[UnsubscribeResponse]] = {
    for {
      response <- SNSSubscribeService.unsubscribeEmail(mail)
      _ <- IO.fromFuture(IO(dbService.unsubscribeFromMailingList(mail))) //
    } yield response

  }
}

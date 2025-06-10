package org.service

import cats.effect.IO
import org.service.FileOperationsService.dbService
import org.service.internal.{MailService, SNSSubscribeService}
import org.service.traits.SQSOperationsService
import software.amazon.awssdk.services.sns.model.{SubscribeResponse, UnsubscribeResponse}

class LiveSQSOperationService extends SQSOperationsService {

  override def subscribe(mail: String): IO[SubscribeResponse] = {
    for {
      _ <- IO.delay(
        MailService.sendEmail(mail, "Subscription request", MailService.subscriptionPath)
      )
      response <- SNSSubscribeService.subscribeEmail(mail)
      _ <- IO.fromFuture(IO(dbService.subscribeAtMailingList(mail))) //
    } yield response
  }

  override def unsubscribe(mail: String): IO[UnsubscribeResponse] = {
    for {
      _ <- IO.delay(
        MailService.sendEmail(mail, "Unsubscription request", MailService.unsubscriptionPath)
      )
      response <- SNSSubscribeService.unsubscribeEmail(mail)
      _ <- IO.fromFuture(IO(dbService.unsubscribeFromMailingList(mail))) //
    } yield response

  }
}

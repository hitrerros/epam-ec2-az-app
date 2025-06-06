package org.service

import cats.effect.IO
import org.db.model.MailingListRecord
import org.service.FileOperationsService.dbService
import org.service.internal.MailService
import org.service.traits.SQSOperationsService

class LiveSQSOperationService extends SQSOperationsService {

  override def subscribe(mail: String): IO[Option[MailingListRecord]] = {
        for {
      _ <- IO.delay(
        MailService.sendEmail(mail, "Hello", MailService.subscriptionPath)
      )
          lines <- IO.fromFuture(IO(dbService.subscribeAtMailingList(mail)))//
     } yield Some(MailingListRecord(1,mail))
  }

  override def unsubscribe(mail: String): IO[Option[MailingListRecord]] = {
    for {
      _ <- IO.delay(
        MailService.sendEmail(mail, "Hello", MailService.unsubscriptionPath)
      )
      lines <- IO.fromFuture(IO(dbService.unsubscribeFromMailingList(mail)))//
    } yield Some(MailingListRecord(1,mail))

  }
}

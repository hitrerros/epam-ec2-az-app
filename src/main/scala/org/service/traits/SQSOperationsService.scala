package org.service.traits

import cats.effect.IO
import org.db.model.MailingListRecord
import org.service.LiveSQSOperationService

trait SQSOperationsService {
  def subscribe(mail : String): IO[Option[MailingListRecord]]
  def unsubscribe(mail : String): IO[Option[MailingListRecord]]
}

object SQSOperationsService {
  implicit val SQSOperationsService: SQSOperationsService = new LiveSQSOperationService()
}

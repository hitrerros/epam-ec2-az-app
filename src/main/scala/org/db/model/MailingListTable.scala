package org.db.model

import slick.jdbc.PostgresProfile.api._

class MailingListTable(tag: Tag)
    extends Table[MailingListRecord](tag, "mailing_list") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def mail = column[String]("mail")

  def * = (id, mail) <> (MailingListRecord.tupled, MailingListRecord.unapply)
}

package org.service.internal

import jakarta.mail.internet.{InternetAddress, MimeMessage}
import jakarta.mail._
import org.service.configuration.ConfigurationService

import java.util.Properties
import scala.io.Source

object MailService {

  val subscriptionPath = "templates/subscribe.html"
  val unsubscriptionPath = "templates/unsubscribe.html"

  private def renderTemplate(templatePath : String, mail : String) : String = {
    val stream = getClass.getClassLoader.getResourceAsStream(templatePath)
    require(stream != null, s"Template '$templatePath' not found in resources.")
    val source = Source.fromInputStream(stream, "UTF-8")
    val str = try source.mkString finally source.close()

    str.replace("%mail%",mail)
  }

  def sendEmail(to: String, subject: String, templatePath: String): Unit = {
    val username = ConfigurationService.email
    val password = ConfigurationService.emailPassword

    // SMTP server configuration
    val props = new Properties()
    props.put("mail.smtp.auth", "true")
    props.put("mail.smtp.starttls.enable", "true")
    props.put("mail.smtp.host", "smtp.gmail.com")
    props.put("mail.smtp.port", "587")

    val session = Session.getInstance(props, new Authenticator {
      override protected def getPasswordAuthentication =
        new PasswordAuthentication(username, password)
    })

    try {
      val address = InternetAddress.parse(to)(0).toString
      val body = renderTemplate(templatePath,address)

      val message = new MimeMessage(session)
      message.setFrom(new InternetAddress(username))
      message.setRecipients(Message.RecipientType.TO, address)
      message.setSubject(subject)
      message.setContent(body, "text/html")

      Transport.send(message)
   } catch {
      case e: MessagingException =>
        e.printStackTrace()
    }
  }

}


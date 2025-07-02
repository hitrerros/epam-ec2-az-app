package org.service.internal

import cats.effect.IO
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.lambda.LambdaClient
import software.amazon.awssdk.services.lambda.model._

import java.nio.charset.StandardCharsets
import scala.util.{Failure, Success, Try}

object LambdaInvocationService {

  def callLambda(functionName: String): IO[String] = IO {
    val lambda = LambdaClient.builder().build()

    val payloadBytes = SdkBytes.fromString("", StandardCharsets.UTF_8)

    val request = InvokeRequest
      .builder()
      .functionName(functionName)
      .invocationType("RequestResponse") // synchronous
      .payload(payloadBytes)
      .build()

    Try(lambda.invoke(request)) match {
      case Success(response) =>
        val responseString = response.payload().asUtf8String()
        lambda.close()
        responseString
      case Failure(exception) => s"Lambda invocation failed: ${exception}"
    }
  }

}

package org.service.internal

import cats.effect.IO
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.lambda.LambdaClient
import software.amazon.awssdk.services.lambda.model._

import scala.util.{Failure, Success, Using}

object LambdaInvocationService {

  def callLambda(functionName: String): IO[String] = IO {
    val lambda = LambdaClient.builder().build()

    val request = InvokeRequest
      .builder()
      .functionName(functionName)
      .invocationType("RequestResponse") // synchronous
      .payload(SdkBytes.fromUtf8String(""))
      .build()

    Using(lambda) { lambdaClient =>
      val response = lambdaClient.invoke(request)
      response.payload().asUtf8String()
    } match {
      case Success(responseString) => responseString
      case Failure(exception) => s"Lambda invocation failed: ${exception}"
    }
  }

}

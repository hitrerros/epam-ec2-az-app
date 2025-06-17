package org.main

import cats.effect.kernel.Resource
import cats.effect.std.Supervisor
import cats.effect.{ExitCode, IO, IOApp}
import org.controller._
import org.http4s.Response.http4sKleisliResponseSyntaxOptionT
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Router
import org.service.internal.SNSCronService.cronStream
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sns.SnsAsyncClient
import software.amazon.awssdk.services.sqs.SqsAsyncClient

object AppServer extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {

    val httpAppList = Router(
      "/" -> GeneralOperationsRoutes.routes,
      "/files" -> FileOperationsRoutes.routes,
      "/info" -> FileMetadataRoutes.routes,
      "/queues" -> SQSOperationsRoutes.routes,
      "/lambda" -> LambdaInvocationRoutes.routes,
    )

    val sqsClientResource: Resource[IO, SqsAsyncClient] =
      Resource.make(IO {
        SqsAsyncClient
          .builder()
          .region(Region.US_WEST_2)
          .build()
      })(client => IO(client.close()))

    val snsClientResource: Resource[IO, SnsAsyncClient] =
      Resource.make(IO {
        SnsAsyncClient
          .builder()
          .region(Region.US_WEST_2)
          .build()
      })(client => IO(client.close()))

    (for {
      sqs <- sqsClientResource
      sns <- snsClientResource
      supervisor <- Supervisor[IO]
      _ <- Resource.eval(
        supervisor.supervise(cronStream(sqs, sns).compile.drain)
      )
      _ <- BlazeServerBuilder[IO]
        .bindHttp(80, "0.0.0.0")
        .withHttpApp(httpAppList.orNotFound)
        .resource
    } yield ()).useForever.as(ExitCode.Success)
  }

}

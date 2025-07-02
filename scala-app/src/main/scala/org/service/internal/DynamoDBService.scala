package org.service.internal

import cats.effect.IO
import org.service.configuration.ConfigurationService
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.model._

import scala.jdk.CollectionConverters._
import java.util.concurrent.CompletableFuture

object DynamoDBService {

  val client: DynamoDbAsyncClient = DynamoDbAsyncClient
    .builder()
    .region(Region.US_WEST_2)
    .credentialsProvider(DefaultCredentialsProvider.create())
    .build()

  def fromCompletableFuture[A](thunk: => CompletableFuture[A]): IO[A] =
    IO.async_[A] { cb =>
      thunk.handle[Unit] { (result: A, err: Throwable) =>
        if (err != null) cb(Left(err)) else cb(Right(result))
      }
    }

  def updateCount(filename : String,field : String): IO[UpdateItemResponse] = {
    val request = UpdateItemRequest.builder()
      .tableName(ConfigurationService.dynamoTable)
      .key(Map(
        "image_name" -> AttributeValue.builder().s(filename).build()
      ).asJava)
      .updateExpression(s"SET ${field} = if_not_exists(${field}, :zero) + :inc")
      .expressionAttributeValues(Map(
        ":inc"  -> AttributeValue.builder().n("1").build(),
        ":zero" -> AttributeValue.builder().n("0").build()
      ).asJava)
      .returnValues(ReturnValue.UPDATED_NEW)
      .build()

    fromCompletableFuture(client.updateItem(request)) // use async client
  }
}

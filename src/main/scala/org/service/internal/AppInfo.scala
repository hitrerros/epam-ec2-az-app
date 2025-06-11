package org.service.internal

import cats.effect.IO
import org.service.configuration.ConfigurationService
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import software.amazon.awssdk.services.ec2.Ec2Client
import software.amazon.awssdk.services.ec2.model.{DescribeInstancesRequest, Filter}

object AppInfo {
  private val tagKey: String = "AppName"
  val logger: IO[SelfAwareStructuredLogger[IO]] = Slf4jLogger.create[IO]

  def getAppUrl: String = {
    ConfigurationService.env match {
      case "local"     => "http:\\\\localhost:80\\"
      case _           => s"http:\\\\${determineCloudUrl}\\"
    }
  }

  private def determineCloudUrl: String = {

    val ec2: Ec2Client = Ec2Client.create

    val request: DescribeInstancesRequest =
      DescribeInstancesRequest.builder
        .filters(
          Filter.builder
            .name("tag:" + tagKey)
            .values(ConfigurationService.appName)
            .build,
          Filter.builder().name("instance-state-name").values("running").build()
        )
        .build

    try {
      val describeInstancesResponse = ec2.describeInstances(request)
      val instance =
        describeInstancesResponse.reservations().get(0).instances().get(0)
      instance.publicIpAddress
    } catch {
      case e:  Exception =>
        println(e.getMessage)
        "N/A"

    } finally {
        ec2.close()
    }

  }
}

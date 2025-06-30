package org.service

import org.service.configuration.ConfigurationService

import scala.io.Source

object FrontendService {
  def getIndexTemplateHtml : String = {
    val stream = getClass.getClassLoader.getResourceAsStream(ConfigurationService.indexHtmlPath)
    require(stream != null, s"Template '${ConfigurationService.indexHtmlPath}' not found in resources.")
    val source = Source.fromInputStream(stream, "UTF-8")
    try source.mkString finally source.close()
  }

}

package de.jaraco

import de.jaraco.config.ConfigurationService
import java.util.*
import java.util.logging.Logger

fun main(args: Array<String>) {
  val logger: Logger = Logger.getLogger("main")
  logger.info("test Parameter: ${args.contentToString()}")
  logger.info("Test")
  ConfigurationService.getInstance().configureCustomWireMock()

}

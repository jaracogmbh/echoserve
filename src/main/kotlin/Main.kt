package de.jaraco

import de.jaraco.config.ConfigurationService
import de.jaraco.exception.NoConfigDirectoryGivenException
import java.util.*
import java.util.logging.Logger

fun main(args: Array<String>) {
  val logger: Logger = Logger.getLogger("main")
  logger.info("test Parameter: ${args.contentToString()}")
  if(args.isEmpty()){
    throw IllegalArgumentException("No modus given")
  }else {
    val modus = args[0]
    val configurationService = ConfigurationService.getInstance()
    if (modus == "docker") {
      if (args.size == 2) {
        val configFilename = args[1]
        configurationService.configureCustomWireMock(modus, configFilename)
      } else {
        throw NoConfigDirectoryGivenException("No config directory given!")
      }
    } else if (modus == "local") {
      configurationService.configureCustomWireMock(modus, null)

    } else {
      throw IllegalArgumentException("Modus $modus not supported")
    }
  }

}

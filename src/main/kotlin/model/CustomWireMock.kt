package de.jaraco.model

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import java.util.logging.Logger

class CustomWireMock(
  val port: Int,
  var fileLocation: String,
  val mode: String
) {

  private var logger: Logger = Logger.getLogger(this.javaClass.name)
  lateinit var wireMockServer: WireMockServer;
  fun startWireMockServer() {
    this.wireMockServer.start()
  }

  fun stopWireMockServer() {
    wireMockServer.stop()
  }

  fun resetWireMockServer() {
    wireMockServer.resetAll()
  }

  fun isWireMockServerRunning(): Boolean {
    return wireMockServer.isRunning
  }

  fun createWireMockServer() {
    if(mode == "docker") {
      logger.info("Creating WireMockServer with port: $port and fileLocation: $fileLocation")
      wireMockServer = WireMockServer(options().port(port).usingFilesUnderDirectory(fileLocation))
    }
    else {
      fileLocation = "src/main/resources"
      wireMockServer = WireMockServer(options().usingFilesUnderClasspath(fileLocation).port(port))
    }
  }

}

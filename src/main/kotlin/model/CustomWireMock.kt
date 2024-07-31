package de.jaraco.model

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options

class CustomWireMock(
  val port: Int,
  var fileLocation: String = "src/test/resources",
) {

  var wireMockServer: WireMockServer = WireMockServer(options().port(port).usingFilesUnderDirectory(fileLocation))
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



}

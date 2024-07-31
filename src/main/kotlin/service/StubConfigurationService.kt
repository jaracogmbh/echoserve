package de.jaraco.service



import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.*
import java.util.logging.Logger

class StubConfigurationService private constructor() {

  companion object {
    private val instance = StubConfigurationService()
    fun getInstance(): StubConfigurationService {
      return instance
    }
  }

  private var logger: Logger = Logger.getLogger(this.javaClass.name)
  fun configureGetStub(url : String, response : String, contentType: String = "application/json", hostname: String, port: Int, status: Int){
    logger.info("Configuring GET stub for url: $url")
    configureFor(hostname, port)
    stubFor(
      get(urlEqualTo(url))
        .willReturn(
          aResponse()
            .withStatus(status)
            .withHeader("Content-Type", contentType)
            .withBodyFile(response)
        )
    )
  }

  fun configurePostStub(url : String, response : String, contentType: String = "application/json", hostname: String, port: Int){
    logger.info("Configuring POST stub for url: $url")
    configureFor(hostname, port)
    stubFor(
      post(urlEqualTo(url))
        .willReturn(
          aResponse()
            .withHeader("Content-Type", contentType)
            .withBody(response)
        )
    )
  }
  fun configureGetParameterStub(
    url : String,
    response : String,
    contentType: String = "application/json",
    hostname: String,
    port: Int,
    status: Int,
    paramName: String,
    param: String
  ){
    logger.info("Configuring GET stub for url: $url")
    configureFor(hostname, port)
    stubFor(
      get(urlPathTemplate("$url/{$paramName}"))
        .withPathParam(paramName, equalTo(param))
        .willReturn(
          aResponse()
            .withStatus(status)
            .withHeader("Content-Type", contentType)
            .withBodyFile(response)
        )
    )
  }
}

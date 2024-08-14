package de.jaraco.service



import com.github.tomakehurst.wiremock.client.WireMock.*
import java.io.File
import java.util.logging.Logger

class StubConfigurationService private constructor() {

    private var logger: Logger = Logger.getLogger(this.javaClass.name)

    companion object {
        private val instance = StubConfigurationService()
        fun getInstance(): StubConfigurationService {
            return instance
        }
    }
    fun configurePostStub(
        url : String,
        response : String,
        contentType: String = "application/json",
        hostname: String,
        port: Int,
        regex: String,
        status: Int,
    ){
        logger.info("Configuring POST stub for url: $url")
        configureFor(hostname, port)
        stubFor(
          post(urlMatching(url))
              .withRequestBody(matching(regex))
              .willReturn(
                aResponse()
                    .withStatus(status)
                    .withHeader("Content-Type", contentType)
                    .withBodyFile(response)
            )
        )
    }

    fun configureGetStub(
        url : String,
        response : String,
        contentType: String = "application/json",
        hostname: String,
        port: Int,
        status: Int,
    ){
        logger.info("Configuring GET stub for url: $url")
        configureFor(hostname, port)
        stubFor(
          get(urlMatching(url))
            .willReturn(
              aResponse()
                .withStatus(status)
                .withHeader("Content-Type", contentType)
                .withBodyFile(response)
            )
        )
    }

    fun configurePutStub(
        url : String,
        response : String,
        contentType: String = "application/json",
        hostname: String,
        port: Int,
        status: Int,
        regex: String,
    ){
        logger.info("Configuring PUT stub for url: $url")
        configureFor(hostname, port)
        stubFor(
        put(urlMatching(url))
            .withRequestBody(matching(regex))
            .willReturn(
                aResponse()
                    .withStatus(status)
                    .withHeader("Content-Type", contentType)
                    .withBodyFile(response)
            )
        )
    }

    fun configureDeleteStub(
        url : String,
        response : String,
        contentType: String = "application/json",
        hostname: String,
        port: Int,
        status: Int,
    ){
        logger.info("Configuring DELETE stub for url: $url")
        configureFor(hostname, port)
        stubFor(
        delete(urlMatching(url))
            .willReturn(
            aResponse()
                .withStatus(status)
                .withHeader("Content-Type", contentType)
                .withBodyFile(response)
            )
        )
    }
}

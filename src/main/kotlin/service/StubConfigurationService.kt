package de.jaraco.service

import com.github.tomakehurst.wiremock.client.WireMock.*
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
        url: String,
        response: String,
        contentType: String = "application/json",
        hostname: String,
        port: Int,
        regex: String,
        status: Int,
    ) {
        logger.info("Configuring POST stub for url: $url")
        configureFor(hostname, port)
        stubFor(
            post(urlMatching(url)).withRequestBody(matching(regex)).willReturn(
                    aResponse().withStatus(status).withHeader("Content-Type", contentType).withBodyFile(response)
                )
        )
    }

    fun configureGetStub(
        url: String,
        response: String,
        contentType: String = "application/json",
        hostname: String,
        port: Int,
        status: Int,
    ) {
        logger.info("Configuring GET stub for url: $url")
        configureFor(hostname, port)
        stubFor(
            get(urlMatching(url)).willReturn(
                    aResponse().withStatus(status).withHeader("Content-Type", contentType).withBodyFile(response)
                )
        )
    }

    fun configurePutStub(
        url: String,
        response: String,
        contentType: String = "application/json",
        hostname: String,
        port: Int,
        status: Int,
        regex: String,
    ) {
        logger.info("Configuring PUT stub for url: $url")
        configureFor(hostname, port)
        stubFor(
            put(urlMatching(url)).withRequestBody(matching(regex)).willReturn(
                    aResponse().withStatus(status).withHeader("Content-Type", contentType).withBodyFile(response)
                )
        )
    }

    fun configureDeleteStub(
        url: String,
        response: String,
        contentType: String = "application/json",
        hostname: String,
        port: Int,
        status: Int,
    ) {
        logger.info("Configuring DELETE stub for url: $url")
        configureFor(hostname, port)
        stubFor(
            delete(urlMatching(url)).willReturn(
                    aResponse().withStatus(status).withHeader("Content-Type", contentType).withBodyFile(response)
                )
        )
    }

    // ~/configure endpoint handlers
    fun configureGetStubKtor(
        url: String,
        hostname: String,
        responseBody: String,
        contentType: String = "application/json",
        port: Int,
        status: Int,
    ) {
        logger.info("Configuring GET stub for url: $url with direct response body on hostname: $hostname")
        configureFor(hostname, port)
        stubFor(
            get(urlMatching(url)).willReturn(
                    aResponse().withStatus(status).withHeader("Content-Type", contentType).withBody(responseBody)
                )
        )
    }

    fun configurePostStubKtor(
        url: String,
        hostname: String,
        responseBody: String,
        contentType: String = "application/json",
        port: Int,
        requestBody: String,
        status: Int,
        isRequestBodyRegex: Boolean = false,
    ) {
        logger.info("Configuring POST stub for url: $url with direct response body on hostname: $hostname")
        configureFor(hostname, port)

        val requestBodyPattern = if (isRequestBodyRegex) {
            matching(requestBody)  // Ensure requestBody is "(?s).*" or "[\\s\\S]*"
        } else {
            equalTo(requestBody)
        }

        stubFor(
            post(urlMatching(url)).withRequestBody(requestBodyPattern).willReturn(
                    aResponse().withStatus(status).withHeader("Content-Type", contentType).withBody(responseBody)
                )
        )
    }

    fun configurePutStubKtor(
        url: String,
        hostname: String,
        responseBody: String,
        contentType: String = "application/json",
        port: Int,
        status: Int,
        requestBody: String,
        isRequestBodyRegex: Boolean = false,
    ) {
        logger.info("Configuring PUT stub for url: $url with direct response body on hostname: $hostname")
        configureFor(hostname, port)

        val requestBodyPattern = if (isRequestBodyRegex) {
            matching(requestBody)
        } else {
            equalTo(requestBody)
        }

        stubFor(
            put(urlMatching(url)).withRequestBody(requestBodyPattern).willReturn(
                    aResponse().withStatus(status).withHeader("Content-Type", contentType).withBody(responseBody)
                )
        )
    }

    fun configureDeleteStubKtor(
        url: String,
        hostname: String,
        responseBody: String,
        contentType: String = "application/json",
        port: Int,
        status: Int,
    ) {
        logger.info("Configuring DELETE stub for url: $url with direct response body on hostname: $hostname")
        configureFor(hostname, port)
        stubFor(
            delete(urlMatching(url)).willReturn(
                    aResponse().withStatus(status).withHeader("Content-Type", contentType).withBody(responseBody)
                )
        )
    }
}

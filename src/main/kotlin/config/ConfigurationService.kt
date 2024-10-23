package de.jaraco.config

import de.jaraco.model.CustomWireMock
import de.jaraco.model.StubConfiguration
import de.jaraco.properties.PropertyLoader
import de.jaraco.service.StubConfigurationService
import java.util.logging.Logger

import java.util.*

/**
 * I think here you had the same problem with double code as you did in the ConfigurationService. Here the same
 * argument applys. Jan and I think that you can keep it as it is since the stubs can be configured to many specific
 * use cases. If you think there is a way to reduce some of the double code you can try. But for now, we think it is fine.
 */

class ConfigurationService private constructor(
    private val propertyLoader: PropertyLoader = PropertyLoader.getInstance(),
    private val stubConfigurationService: StubConfigurationService = StubConfigurationService.getInstance()
) {
    companion object {
        private val instance = ConfigurationService()
        fun getInstance(): ConfigurationService {
            return instance
        }
    }

    private var logger: Logger = Logger.getLogger(this.javaClass.name)

    private lateinit var customWireMock: CustomWireMock

    fun configureCustomWireMockBasic(properties: Properties, mode: String): CustomWireMock {
        val port = propertyLoader.extractPort(properties)
        logger.info("Configuring CustomWireMock with port: $port")
        val fileLoc = propertyLoader.extractFileLocation(properties)
        val customWireMock = CustomWireMock(port.toInt(), fileLoc, mode)
        customWireMock.createWireMockServer()
        return customWireMock
    }

    fun resetCustomWireMock() {
        if (::customWireMock.isInitialized) {
            customWireMock.resetWireMockServer()
            logger.info("WireMock server reset")
        } else {
            logger.warning("Cannot reset WireMock server, it is not initialized")
        }
    }

    private fun configureStubs(properties: Properties) {
        logger.info("Configuring stubs")
        val hostname = propertyLoader.extractHostname(properties)
        val port = propertyLoader.extractPort(properties)
        val requestProperties: Map<String, Properties> = propertyLoader.getRequestConfigurations(properties)
        for ((key, value) in requestProperties) {
            logger.info("Request: $key; value: $value")
            val url = propertyLoader.extractUrl(value)
            val response = propertyLoader.extractResponse(value)
            val contentType = propertyLoader.extractContentType(value)
            val statusCode = propertyLoader.extractStatusCode(value)
            when (val requestType = propertyLoader.extractRequestType(value)) {
                "GET" -> {
                    stubConfigurationService.configureGetStub(
                        url, response, contentType, hostname, port.toInt(), statusCode.toInt()
                    )
                }

                "POST" -> {
                    val requestBody = propertyLoader.extractRequestBody(value)
                    stubConfigurationService.configurePostStub(
                        url, response, contentType, hostname, port.toInt(), requestBody, statusCode.toInt()
                    )
                }

                "PUT" -> {
                    val requestBody = propertyLoader.extractRequestBody(value)
                    stubConfigurationService.configurePutStub(
                        url, response, contentType, hostname, port.toInt(), statusCode.toInt(), requestBody
                    )
                }

                "DELETE" -> stubConfigurationService.configureDeleteStub(
                    url, response, contentType, hostname, port.toInt(), statusCode.toInt()
                )

                else -> logger.warning("Request type $requestType not supported")
            }
        }
    }

    fun configureCustomWireMock(mode: String, filename: String?) {
        val properties = propertyLoader.loadPropertyFile(filename, mode)
        customWireMock = configureCustomWireMockBasic(
            properties, mode
        ) // assigning here, to make sure it's available for ktor config
        customWireMock.startWireMockServer()
        configureStubs(properties)
        logger.info("WireMock server started")
        val isRunning = customWireMock.isWireMockServerRunning()
        if (isRunning) {
            logger.info("WireMock server is running")
        } else {
            logger.warning("WireMock server is not running")
        }
    }


    //incoming stub config from /configure endpoint
    fun configureStubFromPayload(stubConfig: StubConfiguration) {
        val port = customWireMock.port

        when (stubConfig.requestType.uppercase()) {
            "GET" -> {
                if (stubConfig.responseBody != null) {
                    stubConfigurationService.configureGetStubKtor(
                        url = stubConfig.url,
                        hostname = stubConfig.hostname,
                        responseBody = stubConfig.responseBody,
                        contentType = stubConfig.contentType,
                        port = port,
                        status = stubConfig.statusCode,
                    )
                } else {
                    logger.warning("No response or responseBody provided for GET request")
                }
            }

            "POST" -> {
                val requestBody = stubConfig.requestBody ?: ".*"
                if (stubConfig.responseBody != null) {
                    stubConfigurationService.configurePostStubKtor(
                        url = stubConfig.url,
                        hostname = stubConfig.hostname,
                        responseBody = stubConfig.responseBody,
                        contentType = stubConfig.contentType,
                        port = port,
                        requestBody = requestBody,
                        status = stubConfig.statusCode,
                        isRequestBodyRegex = stubConfig.isRequestBodyRegex
                    )
                } else {
                    logger.warning("No response or responseBody provided for POST request")
                }
            }

            "PUT" -> {
                val requestBody = stubConfig.requestBody ?: ".*"
                if (stubConfig.responseBody != null) {
                    stubConfigurationService.configurePutStubKtor(
                        url = stubConfig.url,
                        hostname = stubConfig.hostname,
                        responseBody = stubConfig.responseBody,
                        contentType = stubConfig.contentType,
                        port = port,
                        requestBody = requestBody,
                        status = stubConfig.statusCode,
                        isRequestBodyRegex = stubConfig.isRequestBodyRegex
                    )
                } else {
                    logger.warning("No response or responseBody provided for PUT request")
                }
            }

            "DELETE" -> {
                if (stubConfig.responseBody != null) {
                    stubConfigurationService.configureDeleteStubKtor(
                        url = stubConfig.url,
                        hostname = stubConfig.hostname,
                        responseBody = stubConfig.responseBody,
                        contentType = stubConfig.contentType,
                        port = port,
                        status = stubConfig.statusCode,
                    )
                } else {
                    logger.warning("No response or responseBody provided for DELETE request")
                }
            }

            else -> {
                logger.warning("Unsupported request type: ${stubConfig.requestType}")
            }
        }
    }
}

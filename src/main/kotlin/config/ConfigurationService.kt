package de.jaraco.config

import de.jaraco.model.CustomWireMock
import de.jaraco.properties.PropertyLoader
import de.jaraco.service.StubConfigurationService
import java.util.Properties
import java.util.logging.Logger

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


  fun configureCustomWireMockBasic(properties: Properties, modus: String): CustomWireMock {
    val port = propertyLoader.extractPort(properties)
    logger.info("Configuring CustomWireMock with port: $port")
    val fileLoc = propertyLoader.extractFileLocation(properties)
    val customWireMock = CustomWireMock(port.toInt(), fileLoc, modus)
    customWireMock.createWireMockServer()
    return customWireMock
  }

  fun configureStubs(properties: Properties) {
    logger.info("Configuring stubs")
    val hostname = propertyLoader.extractHostname(properties)
    val port = propertyLoader.extractPort(properties)
    val requestProperties : Map<String, Properties> = propertyLoader.getRequestConfigurations(properties)
    for ((key, value) in requestProperties) {
      logger.info("Request: $key; value: $value")
      val url = propertyLoader.extractUrl(value)
      val response = propertyLoader.extractResponse(value)
      val contentType = propertyLoader.extractContentType(value)
      val statusCode = propertyLoader.extractStatusCode(value)
      when (val requestType = propertyLoader.extractRequestType(value)) {
        "GET" -> {
          stubConfigurationService.configureGetStub(url, response, contentType, hostname, port.toInt(), statusCode.toInt())}
        "POST" -> {
          val requestBody = propertyLoader.extractRequestBody(value)
          stubConfigurationService.configurePostStub(url, response, contentType, hostname, port.toInt(), requestBody, statusCode.toInt())
        }
        "PUT" -> {
          val requestBody = propertyLoader.extractRequestBody(value)
          stubConfigurationService.configurePutStub(url, response, contentType, hostname, port.toInt(), statusCode.toInt(), requestBody)
        }
        "DELETE" -> stubConfigurationService.configureDeleteStub(url, response, contentType, hostname, port.toInt(), statusCode.toInt())
        else -> logger.warning("Request type $requestType not supported")
      }
    }
  }

  fun configureCustomWireMock(modus: String, filename: String?) {
    val properties = propertyLoader.loadPropertyFile(filename, modus)
    val customWireMock = configureCustomWireMockBasic(properties, modus)
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

}

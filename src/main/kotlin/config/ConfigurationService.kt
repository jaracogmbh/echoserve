package de.jaraco.config

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
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
  private val properties = propertyLoader.loadPropertyFile("config.properties")

  fun configureCustomWireMockBasic(): CustomWireMock {
    val port = propertyLoader.extractPort(properties)
    logger.info("Configuring CustomWireMock with port: $port")
    val mode = propertyLoader.extractMode(properties)
    if(mode == "docker"){
        val fileLocation = propertyLoader.extractFileLocation(properties)
      logger.info("Configuring CustomWireMock with fileLocation: $fileLocation")
        val customWireMock = CustomWireMock(port.toInt(), fileLocation)
        return customWireMock
    }else{
        val customWireMock = CustomWireMock(port.toInt())
        return customWireMock
    }
  }

  fun configureStubs() {
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
      val parameter: Boolean = propertyLoader.extractWithParameter(value).toBoolean()
      when (val requestType = propertyLoader.extractRequestType(value)) {
        "GET" -> {
          if(parameter){
            val paramName = propertyLoader.extractParamName(value)
            val param = propertyLoader.extractParam(value)
            stubConfigurationService.configureGetParameterStub(url, response, contentType, hostname, port.toInt(), statusCode.toInt(), paramName, param)
          } else {
            stubConfigurationService.configureGetStub(url, response, contentType, hostname, port.toInt(), statusCode.toInt())
          }
          stubConfigurationService.configureGetStub(url, response, contentType, hostname, port.toInt(), statusCode.toInt())}
        "POST" -> stubConfigurationService.configurePostStub(url, response, contentType, hostname, port.toInt())
        else -> logger.warning("Request type $requestType not supported")
      }
    }
  }

  fun configureCustomWireMock(){
    val customWireMock = configureCustomWireMockBasic()
    //configureStubs(customWireMock)
    customWireMock.startWireMockServer()
    configureStubs()
    logger.info("WireMock server started")
    val isRunning = customWireMock.isWireMockServerRunning()
    if (isRunning) {
      logger.info("WireMock server is running")
    } else {
      logger.warning("WireMock server is not running")
    }
  }

}

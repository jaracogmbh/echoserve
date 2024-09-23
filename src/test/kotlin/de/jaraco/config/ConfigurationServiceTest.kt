package de.jaraco.config

import de.jaraco.properties.PropertyLoader
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ConfigurationServiceTest {

  private val underTest = ConfigurationService.getInstance()

  @Test
  fun testConfigureCustomWireMockBasic() {
    val propertyLoader = PropertyLoader.getInstance()
    val properties = propertyLoader.loadPropertyFile("config.properties", "local")
    val customWireMock = underTest.configureCustomWireMockBasic(properties, "local")
    val port = customWireMock.port
    val mode = customWireMock.mode
    val fileLocation = customWireMock.fileLocation
    assertEquals(8089, port)
    assertEquals("local", mode)
    assertEquals("src/main/resources", fileLocation)
  }

  @Test
  fun testConfigurationWireMockDocker(){
    val propertyLoader = PropertyLoader.getInstance()
    val path = System.getProperty("user.dir")
    println("Working Directory = $path")
    val properties = propertyLoader.loadPropertyFile("test.properties", "docker")
    val customWireMock = underTest.configureCustomWireMockBasic(properties, "docker")
    val port = customWireMock.port
    val mode = customWireMock.mode
    val fileLocation = customWireMock.fileLocation
    assertEquals(8080, port)
    assertEquals("docker", mode)
    assertEquals("/data", fileLocation)
  }
}

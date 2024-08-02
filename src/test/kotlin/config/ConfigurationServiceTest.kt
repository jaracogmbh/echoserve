package config

import de.jaraco.config.ConfigurationService
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ConfigurationServiceTest {

  val underTest = ConfigurationService.getInstance()

  /*//@Test
  fun testConfigureCustomWireMockBasic() {
    val customWireMock = underTest.configureCustomWireMockBasic(null, "modus")
    val port = customWireMock.port
    assertEquals(8089, port)
  }*/
}

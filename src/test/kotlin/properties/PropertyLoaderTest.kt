package properties

import de.jaraco.exception.NoPropertiesInPropertyFileException
import de.jaraco.exception.UnknownModusException
import de.jaraco.properties.PropertyLoader
import org.junit.jupiter.api.assertThrows
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PropertyLoaderTest {
  private val underTest: PropertyLoader = PropertyLoader.getInstance()

  @Test
  fun testLoadPropertyFile() {
    val properties: Properties = underTest.loadPropertyFile("test.properties", "docker")
    assert(properties.isNotEmpty())
    assert(properties.containsKey("hostname"))
    assert(properties.containsKey("port"))
  }

  @Test
  fun testGetRequestPrefixList() {
    val properties: Properties = underTest.loadPropertyFile("requestPrefix.properties", "docker")
    val prefixes = underTest.getRequestPrefixList(properties)
    println(prefixes)
    assertEquals(3, prefixes.size)
    assertTrue(prefixes.contains("request1"))
    assertTrue(prefixes.contains("request2"))
    assertTrue(prefixes.contains("request3"))
  }

  @Test
  fun testCreateProperties(){
    val properties: Properties = underTest.loadPropertyFile("requestPrefix.properties", "docker")
    val requestProperties : Map<String, Properties> = underTest.getRequestConfigurations(properties)
    assertEquals(3, requestProperties.size)
    assertTrue(requestProperties.containsKey("request1"))
    assertTrue(requestProperties.containsKey("request2"))
    assertTrue(requestProperties.containsKey("request3"))
    val request1 = requestProperties["request1"]
    assertEquals("GET", underTest.extractRequestType(request1!!))
    assertEquals("application/json", underTest.extractContentType(request1))
    assertEquals("earth.json", underTest.extractResponse(request1))
    assertEquals("/getWorld\\?([a-z]*)=([0-9]*)", underTest.extractUrl(request1))
    assertEquals("200", underTest.extractStatusCode(request1))

    val request2 = requestProperties["request2"]
    assertEquals("POST", underTest.extractRequestType(request2!!))
    assertEquals("application/json", underTest.extractContentType(request2))
    assertEquals("newWorld.json", underTest.extractResponse(request2))
    assertEquals("/newWorld", underTest.extractUrl(request2))
    assertEquals("200", underTest.extractStatusCode(request2))
    assertEquals(".*", underTest.extractRequestBody(request2))

    val request3 = requestProperties["request3"]
    assertEquals("GET", underTest.extractRequestType(request3!!))
    assertEquals("application/json", underTest.extractContentType(request3))
    assertEquals("allWorld.json", underTest.extractResponse(request3))
    assertEquals("/allWorlds", underTest.extractUrl(request3))
    assertEquals("200", underTest.extractStatusCode(request3))
  }

  @Test
  fun testLocalProperties(){
    val properties: Properties = underTest.loadPropertyFile("config.properties", "local")
    assertEquals("localhost", underTest.extractHostname(properties))
    assertEquals("8089", underTest.extractPort(properties))
    val requestProperties : Map<String, Properties> = underTest.getRequestConfigurations(properties)
    assertEquals(5, requestProperties.size)
    assertTrue(requestProperties.containsKey("request1"))
    assertTrue(requestProperties.containsKey("request2"))
    assertTrue(requestProperties.containsKey("request3"))
    assertTrue(requestProperties.containsKey("request4"))
    assertTrue(requestProperties.containsKey("request5"))
  }

  @Test
  fun testKeyWithoutPrefix(){
    val key = "request1.url"
    val prefix = "request1"
    val result = underTest.keyWithoutPrefix(key, prefix)
    assertEquals("url", result)
  }

  @Test
  fun testNoCorrectModus(){
    assertThrows<UnknownModusException> {
      val properties = underTest.loadPropertyFile("config.properties", "wrong")
    }
  }

  @Test
  fun testEmptyPropertiesFile(){
    assertThrows<NoPropertiesInPropertyFileException> {
        val properties = underTest.loadPropertyFile("empty.properties", "docker")
    }
  }


}

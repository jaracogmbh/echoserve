package properties

import de.jaraco.properties.PropertyLoader
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PropertyLoaderTest {
  private val underTest: PropertyLoader = PropertyLoader.getInstance()

  /*@Test
  fun testLoadPropertyFile() {
    val properties: Properties = underTest.loadPropertyFile("test.properties")
    assert(properties.isNotEmpty())
    assert(properties.containsKey("hostname"))
    assert(properties.containsKey("port"))
  }

  @Test
  fun testGetRequestPrefixList() {
    val properties: Properties = underTest.loadPropertyFile("requestPrefix.properties")
    val prefixes = underTest.getRequestPrefixList(properties)
    println(prefixes)
    assertEquals(3, prefixes.size)
    assertTrue(prefixes.contains("request1"))
    assertTrue(prefixes.contains("request2"))
    assertTrue(prefixes.contains("request3"))
  }

  @Test
  fun testCreateProperties(){
    val properties: Properties = underTest.loadPropertyFile("requestPrefix.properties")
    val requestProperties : Map<String, Properties> = underTest.getRequestConfigurations(properties)
    assertEquals(3, requestProperties.size)
    assertTrue(requestProperties.containsKey("request1"))
    assertTrue(requestProperties.containsKey("request2"))
    assertTrue(requestProperties.containsKey("request3"))
    for(property in requestProperties){
      assertEquals(4, property.value.size)
    }
    val request1 = requestProperties["request1"]
    assertEquals("GET", underTest.extractRequestType(request1!!))
    assertEquals("application/json", underTest.extractContentType(request1))
    assertEquals("{\"response\": \"Hello World\"}", underTest.extractResponse(request1))
    assertEquals("/helloWorld", underTest.extractUrl(request1))

    val request2 = requestProperties["request2"]
    assertEquals("POST", underTest.extractRequestType(request2!!))
    assertEquals("application/json", underTest.extractContentType(request2))
    assertEquals("{\"response\": \"New World\"}", underTest.extractResponse(request2))
    assertEquals("/newWorld", underTest.extractUrl(request2))

    val request3 = requestProperties["request3"]
    assertEquals("GET", underTest.extractRequestType(request3!!))
    assertEquals("application/json", underTest.extractContentType(request3))
    assertEquals("{\"response\": \"All Worlds\"}", underTest.extractResponse(request3))
    assertEquals("/allWorlds", underTest.extractUrl(request3))
  }

  @Test
  fun testKeyWithoutPrefix(){
    val key = "request1.url"
    val prefix = "request1"
    val result = underTest.keyWithoutPrefix(key, prefix)
    assertEquals("url", result)
  }
*/
}

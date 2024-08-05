package de.jaraco.properties

import de.jaraco.exception.NoPropertiesInPropertyFileException
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.util.*
import java.util.logging.Logger

class PropertyLoader private constructor() {

  companion object {
    private val instance = PropertyLoader()
    fun getInstance(): PropertyLoader {
      return instance
    }
  }

  private var logger: Logger = Logger.getLogger(this.javaClass.name)
  private val hostname : String = "hostname"
  private val port : String = "port"
  private val contentType : String = "contentType"
  private val requestType : String = "requestType"
  private val response : String = "response"
  private val url : String = "url"


  @Throws(FileNotFoundException::class, NoPropertiesInPropertyFileException::class)
  fun loadPropertyFile(filename: String?, modus: String): Properties {
    val inputStream: InputStream
    if(modus == "local") {
      logger.info("filename: $filename")
      inputStream = javaClass.classLoader.getResourceAsStream("config.properties")!!
    }else if(modus == "docker") {
      inputStream = java.io.FileInputStream("/data/$filename")
    }else{
      throw IllegalArgumentException("Modus $modus not supported")
    }
    val properties1 = Properties()
    try {
      properties1.load(inputStream)
    } catch (e: IOException) {
      throw FileNotFoundException("file " + filename + " not found, " + e.message)
    }
    if (properties1.isEmpty) {
      throw NoPropertiesInPropertyFileException("Property file $filename seems to be empty!")
    }
    for ((key, value) in properties1) {
      logger.info("key: $key; value: $value")
    }
    return properties1
  }

  fun extractHostname(properties: Properties): String {
    logger.info("Hostname: " + properties.getProperty(hostname))
    return properties.getProperty(hostname)
  }

  fun extractPort(properties: Properties): String {
    logger.info("Port: " + properties.getProperty(port))
    return properties.getProperty(port)
  }

  fun extractContentType(properties: Properties): String {
    logger.info("ContentType: " + properties.getProperty(contentType))
    return properties.getProperty(contentType)
  }

  fun extractRequestType(properties: Properties): String {
    logger.info("RequestType: " + properties.getProperty(requestType))
    return properties.getProperty(requestType)
  }

  fun extractResponse(properties: Properties): String {
    logger.info("Response: " + properties.getProperty(response))
    return properties.getProperty(response)
  }

  fun extractUrl(properties: Properties): String {
    logger.info("Url: " + properties.getProperty(url))
    return properties.getProperty(url)
  }
  fun extractStatusCode(properties: Properties): String {
    return properties.getProperty("statusCode")
  }

  fun extractRequestBody(properties: Properties): String {
    return properties.getProperty("requestBody")
  }

  fun extractFileLocation(properties: Properties): String {
    return properties.getProperty("fileLocation")
  }

  fun keyWithoutPrefix(key: String, prefix: String): String {
    return key.replace("$prefix.", "")
  }

  fun createProperties(properties: Properties, prefix: String): Properties {
    val newProperties = Properties()
    val names = properties.stringPropertyNames()
    for (key in names) {
      if (key.contains("$prefix.")) {
        val value = properties.getProperty(key)
        logger.info("Value:$value")
        val newKey: String = keyWithoutPrefix(key, prefix)
        newProperties.setProperty(newKey, value)
      }
    }
    return newProperties
  }

  fun getRequestPrefixName(key: String): String{
    val index = key.indexOf(".")
    val prefix = key.substring(0, index)
    return prefix
  }

  fun getRequestPrefixList(properties: Properties): ArrayList<String> {
    val prefixList: ArrayList<String> = ArrayList()
    val names = properties.stringPropertyNames()
    for (key in names) {
      if (key.contains("request")) {
        val prefix = getRequestPrefixName(key)
        if(!prefixList.contains(prefix)) {
          prefixList.add(prefix)
        }
      }
    }
    return prefixList
  }

  fun getRequestConfigurations(properties: Properties?): Map<String, Properties> {
    val propertiesMap: MutableMap<String, Properties> = HashMap()

    val prefixList: ArrayList<String> = getRequestPrefixList(properties!!)
    logger.info("request prefix List found: contains " + prefixList.size + " elements")

    for (prefix in prefixList) {
      val curr = createProperties(properties, prefix)
      propertiesMap[prefix] = curr
    }
    logger.info("request configurations found: " + propertiesMap.keys.size + " configurations")
    return propertiesMap
  }


}

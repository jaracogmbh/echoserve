package de.jaraco.properties

import de.jaraco.exception.NoPropertiesInPropertyFileException
import de.jaraco.exception.UnknownModeException
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
    private val hostnameKey: String = "hostname"
    private val portKey: String = "port"
    private val contentTypeKey: String = "contentType"
    private val requestTypeKey: String = "requestType"
    private val responseKey: String = "response"
    private val urlKey: String = "url"

    @Throws(FileNotFoundException::class, NoPropertiesInPropertyFileException::class)
    fun loadPropertyFile(filename: String?, mode: String): Properties {
        val inputStream: InputStream = when (mode) {
            "local" -> {
                logger.info("Loading properties file from classpath: config.properties")
                javaClass.classLoader.getResourceAsStream("config.properties")
                    ?: throw FileNotFoundException("config.properties not found in classpath")
            }

            "docker" -> {
                val filePath = "data/$filename"
                logger.info("Loading properties file from: $filePath")
                java.io.FileInputStream(filePath)
            }

            else -> {
                throw UnknownModeException("Modus $mode not supported")
            }
        }

        val properties = Properties()
        try {
            properties.load(inputStream)
        } catch (e: IOException) {
            throw FileNotFoundException("File $filename not found, ${e.message}")
        }
        if (properties.isEmpty) {
            throw NoPropertiesInPropertyFileException("Property file $filename seems to be empty!")
        }
        for ((key, value) in properties) {
            logger.info("key: $key; value: $value")
        }
        return properties
    }

    fun extractHostname(properties: Properties): String {
        val hostname = properties.getProperty(hostnameKey, "localhost")
        logger.info("Hostname: $hostname")
        return hostname
    }

    fun extractPort(properties: Properties): String {
        val port = properties.getProperty(portKey, "8080")
        logger.info("Port: $port")
        return port
    }

    fun extractContentType(properties: Properties): String {
        val contentType = properties.getProperty(contentTypeKey, "application/json")
        logger.info("ContentType: $contentType")
        return contentType
    }

    fun extractRequestType(properties: Properties): String {
        val requestType = properties.getProperty(requestTypeKey)
        logger.info("RequestType: $requestType")
        return requestType
    }

    fun extractResponse(properties: Properties): String {
        val response = properties.getProperty(responseKey)
        logger.info("Response: $response")
        return response
    }

    fun extractUrl(properties: Properties): String {
        val url = properties.getProperty(urlKey)
        logger.info("Url: $url")
        return url
    }

    fun extractStatusCode(properties: Properties): String {
        val statusCode = properties.getProperty("statusCode", "200")
        logger.info("StatusCode: $statusCode")
        return statusCode
    }

    fun extractRequestBody(properties: Properties): String {
        val requestBody = properties.getProperty("requestBody", ".*")
        logger.info("RequestBody: $requestBody")
        return requestBody
    }

    fun extractFileLocation(properties: Properties): String {
        val fileLocation = properties.getProperty("fileLocation", "src/main/resources")
        logger.info("FileLocation: $fileLocation")
        return fileLocation
    }

    fun keyWithoutPrefix(key: String, prefix: String): String {
        return key.replace("$prefix.", "")
    }

    private fun createProperties(properties: Properties, prefix: String): Properties { // do we need it publicly
        val newProperties = Properties()
        val names = properties.stringPropertyNames()
        for (key in names) {
            if (key.startsWith("$prefix.")) {
                val value = properties.getProperty(key)
                logger.info("Value: $value")
                val newKey: String = keyWithoutPrefix(key, prefix)
                newProperties.setProperty(newKey, value)
            }
        }
        return newProperties
    }

    private fun getRequestPrefixName(key: String): String {
        val index = key.indexOf(".")
        return key.substring(0, index)
    }

    fun getRequestPrefixList(properties: Properties): ArrayList<String> {
        val prefixList: ArrayList<String> = ArrayList()
        val names = properties.stringPropertyNames()
        for (key in names) {
            if (key.contains("request")) {
                val prefix = getRequestPrefixName(key)
                if (!prefixList.contains(prefix)) {
                    prefixList.add(prefix)
                }
            }
        }
        return prefixList
    }

    fun getRequestConfigurations(properties: Properties): Map<String, Properties> {
        val propertiesMap: MutableMap<String, Properties> = HashMap()

        val prefixList: ArrayList<String> = getRequestPrefixList(properties)
        logger.info("Request prefix list found: contains ${prefixList.size} elements")

        for (prefix in prefixList) {
            val curr = createProperties(properties, prefix)
            propertiesMap[prefix] = curr
        }
        logger.info("Request configurations found: ${propertiesMap.keys.size} configurations")
        return propertiesMap
    }
}

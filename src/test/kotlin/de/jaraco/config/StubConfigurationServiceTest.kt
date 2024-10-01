package de.jaraco.service

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import kotlin.test.assertEquals

class StubConfigurationServiceTest {

    private lateinit var wireMockServer: WireMockServer
    private val underTest = StubConfigurationService.getInstance()
    private val hostname = "localhost"
    private val port = 8080

    @BeforeEach
    fun setup() {

        wireMockServer = WireMockServer(WireMockConfiguration.wireMockConfig().port(port))
        wireMockServer.start()
    }

    @AfterEach
    fun teardown() {

        wireMockServer.stop()
    }

    @Test
    fun testConfigureGetStub() {

        val url = "/test-get"
        val responseFileName = "response-get.json"
        val responseContent = "{\"message\": \"GET request successful\"}"
        val contentType = "application/json"
        val status = 200

        writeResponseFile(responseFileName, responseContent)

        underTest.configureGetStub(
            url = url,
            response = responseFileName,
            contentType = contentType,
            hostname = hostname,
            port = port,
            status = status
        )


        val connection = URL("http://$hostname:$port$url").openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connect()

        val responseCode = connection.responseCode
        val responseBody = connection.inputStream.bufferedReader().use { it.readText() }


        assertEquals(status, responseCode)
        assertEquals(responseContent, responseBody)
    }

    @Test
    fun testConfigurePostStub() {

        val url = "/test-post"
        val responseFileName = "response-post.json"
        val responseContent = "{\"message\": \"POST request successful\"}"
        val contentType = "application/json"
        val status = 201
        val requestBodyRegex = ".*"


        writeResponseFile(responseFileName, responseContent)


        underTest.configurePostStub(
            url = url,
            response = responseFileName,
            contentType = contentType,
            hostname = hostname,
            port = port,
            regex = requestBodyRegex,
            status = status
        )


        val connection = URL("http://$hostname:$port$url").openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", contentType)
        connection.outputStream.use { os ->
            os.write("{\"data\": \"test\"}".toByteArray(Charsets.UTF_8))
        }

        val responseCode = connection.responseCode
        val responseBody = connection.inputStream.bufferedReader().use { it.readText() }


        assertEquals(status, responseCode)
        assertEquals(responseContent, responseBody)
    }

    @Test
    fun testConfigurePutStub() {

        val url = "/test-put"
        val responseFileName = "response-put.json"
        val responseContent = "{\"message\": \"PUT request successful\"}"
        val contentType = "application/json"
        val status = 200
        val requestBodyRegex = ".*"


        writeResponseFile(responseFileName, responseContent)


        underTest.configurePutStub(
            url = url,
            response = responseFileName,
            contentType = contentType,
            hostname = hostname,
            port = port,
            status = status,
            regex = requestBodyRegex
        )


        val connection = URL("http://$hostname:$port$url").openConnection() as HttpURLConnection
        connection.requestMethod = "PUT"
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", contentType)
        connection.outputStream.use { os ->
            os.write("{\"data\": \"test\"}".toByteArray(Charsets.UTF_8))
        }

        val responseCode = connection.responseCode
        val responseBody = connection.inputStream.bufferedReader().use { it.readText() }


        assertEquals(status, responseCode)
        assertEquals(responseContent, responseBody)
    }

    @Test
    fun testConfigureDeleteStub() {

        val url = "/test-delete"
        val responseFileName = "response-delete.json"
        val responseContent = "{\"message\": \"DELETE request successful\"}"
        val contentType = "application/json"
        val status = 200


        writeResponseFile(responseFileName, responseContent)


        underTest.configureDeleteStub(
            url = url,
            response = responseFileName,
            contentType = contentType,
            hostname = hostname,
            port = port,
            status = status
        )

        val connection = URL("http://$hostname:$port$url").openConnection() as HttpURLConnection
        connection.requestMethod = "DELETE"
        connection.connect()

        val responseCode = connection.responseCode
        val responseBody = connection.inputStream.bufferedReader().use { it.readText() }


        assertEquals(status, responseCode)
        assertEquals(responseContent, responseBody)
    }

    @Test
    fun testConfigureGetStubKtor() {

        val url = "/test-get-ktor"
        val responseBody = "{\"message\": \"GET Ktor request successful\"}"
        val contentType = "application/json"
        val status = 200


        underTest.configureGetStubKtor(
            url = url,
            hostname = hostname,
            responseBody = responseBody,
            contentType = contentType,
            port = port,
            status = status
        )


        val connection = URL("http://$hostname:$port$url").openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connect()

        val responseCode = connection.responseCode
        val responseContent = connection.inputStream.bufferedReader().use { it.readText() }


        assertEquals(status, responseCode)
        assertEquals(responseBody, responseContent)
    }

    @Test
    fun testConfigurePostStubKtor() {

        val url = "/test-post-ktor"
        val requestBody = "{\"name\": \"Test User\"}"
        val mockedResponseBody = "{\"message\": \"POST Ktor request successful\"}"
        val contentType = "application/json"
        val status = 201
        val isRequestBodyRegex = false


        underTest.configurePostStubKtor(
            url = url,
            hostname = hostname,
            responseBody = mockedResponseBody,
            contentType = contentType,
            port = port,
            requestBody = requestBody,
            status = status,
            isRequestBodyRegex = isRequestBodyRegex
        )


        val connection = URL("http://$hostname:$port$url").openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", contentType)
        connection.outputStream.use { os ->
            os.write(requestBody.toByteArray(Charsets.UTF_8))
        }

        val responseCode = connection.responseCode
        val responseBody = connection.inputStream.bufferedReader().use { it.readText() }


        assertEquals(status, responseCode)
        assertEquals(responseBody, mockedResponseBody)
    }

    @Test
    fun testConfigurePostStubKtorWithRegex() {

        val url = "/test-post-ktor-regex"
        val requestBodyRegex = "(?s).*"
        val actualRequestBody = """
            {
                "name": "Any User",
                "email": "user@example.com"
            }
        """.trimIndent()
        val responseBody = "{\"message\": \"POST Ktor regex request successful\"}"
        val contentType = "application/json"
        val status = 200
        val isRequestBodyRegex = true


        underTest.configurePostStubKtor(
            url = url,
            hostname = hostname,
            responseBody = responseBody,
            contentType = contentType,
            port = port,
            requestBody = requestBodyRegex,
            status = status,
            isRequestBodyRegex = isRequestBodyRegex
        )


        val connection =
            URI("http", null, hostname, port, url, null, null).toURL().openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", contentType)
        connection.outputStream.use { os ->
            os.write(actualRequestBody.toByteArray(Charsets.UTF_8))
        }

        val responseCode = connection.responseCode
        val responseBodyReceived = connection.inputStream.bufferedReader().use { it.readText() }


        assertEquals(status, responseCode)
        assertEquals(responseBody, responseBodyReceived)
    }

    @Test
    fun testConfigurePutStubKtor() {

        val url = "/test-put-ktor"
        val requestBody = "{\"name\": \"Updated User\"}"
        val responseBody = "{\"message\": \"PUT Ktor request successful\"}"
        val contentType = "application/json"
        val status = 200
        val isRequestBodyRegex = false


        underTest.configurePutStubKtor(
            url = url,
            hostname = hostname,
            responseBody = responseBody,
            contentType = contentType,
            port = port,
            status = status,
            requestBody = requestBody,
            isRequestBodyRegex = isRequestBodyRegex
        )


        val connection = URL("http://$hostname:$port$url").openConnection() as HttpURLConnection
        connection.requestMethod = "PUT"
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", contentType)
        connection.outputStream.use { os ->
            os.write(requestBody.toByteArray(Charsets.UTF_8))
        }

        val responseCode = connection.responseCode
        val responseBodyReceived = connection.inputStream.bufferedReader().use { it.readText() }


        assertEquals(status, responseCode)
        assertEquals(responseBody, responseBodyReceived)
    }

    @Test
    fun testConfigureDeleteStubKtor() {

        val url = "/test-delete-ktor"
        val responseBody = "{\"message\": \"DELETE Ktor request successful\"}"
        val contentType = "application/json"
        val status = 200


        underTest.configureDeleteStubKtor(
            url = url,
            hostname = hostname,
            responseBody = responseBody,
            contentType = contentType,
            port = port,
            status = status
        )

        val connection = URL("http://$hostname:$port$url").openConnection() as HttpURLConnection
        connection.requestMethod = "DELETE"
        connection.connect()

        val responseCode = connection.responseCode
        val responseBodyReceived = connection.inputStream.bufferedReader().use { it.readText() }


        assertEquals(status, responseCode)
        assertEquals(responseBody, responseBodyReceived)
    }

    private fun writeResponseFile(fileName: String, content: String) {
        val filesDir = File("src/test/resources/__files")
        if (!filesDir.exists()) {
            filesDir.mkdirs()
        }
        val file = File(filesDir, fileName)
        file.writeText(content)
    }
}

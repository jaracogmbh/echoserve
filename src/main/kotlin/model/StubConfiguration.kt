package de.jaraco.model

import kotlinx.serialization.Serializable

@Serializable
data class StubConfiguration(
    val requestType: String,
    val url: String,
    val hostname: String,
    val response: String? = null,
    val responseBody: String? = null,
    val contentType: String = "application/json",
    val statusCode: Int = 200,
    val requestBody: String? = null,
    val isRequestBodyRegex: Boolean = false,
)

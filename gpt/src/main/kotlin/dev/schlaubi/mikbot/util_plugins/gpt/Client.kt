package dev.schlaubi.mikbot.util_plugins.gpt

import dev.schlaubi.mikbot.util_plugins.gpt.model.MessageRequest
import dev.schlaubi.mikbot.util_plugins.gpt.model.MessageResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*

private const val baseUrl = "https://deppgptrelease221-ep4qq6pqcq-ew.a.run.app/"

object GptClient {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json()
        }
        defaultRequest {
            url.takeFrom(baseUrl)
        }
    }

    suspend fun requestMessages(input: MessageRequest) = client.post {
        contentType(ContentType.Application.Json)
        setBody(input)
    }.body<MessageResponse>()
}

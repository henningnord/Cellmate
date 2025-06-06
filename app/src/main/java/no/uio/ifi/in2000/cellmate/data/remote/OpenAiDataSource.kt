package no.uio.ifi.in2000.cellmate.data.remote

import io.ktor.client.request.setBody
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.http.ContentType
import io.ktor.http.contentType
import no.uio.ifi.in2000.cellmate.data.clientprovider.HttpClientProvider.client
import no.uio.ifi.in2000.cellmate.domain.model.openai.ChatRequest
import no.uio.ifi.in2000.cellmate.domain.model.openai.ChatResponse
import no.uio.ifi.in2000.cellmate.domain.model.openai.Message
import javax.inject.Inject

class OpenAiDataSource @Inject constructor() {

    suspend fun getFunFact(userMessage: String): String {
        val apiKey = ""
        val response: HttpResponse = client.post("https://api.openai.com/v1/chat/completions") {
            headers {
                append("Authorization", "Bearer $apiKey")
            }
            contentType(ContentType.Application.Json)
            setBody(
                ChatRequest(
                    model = "gpt-4-turbo",
                    messages = listOf(
                        Message(
                            "system",
                            "Du er en erfaren energikonsulent. Bruk $userMessage kWt som utgangspunkt og lag én praktisk og gjerne overraskende funfact om hva man kan gjøre med så mye strøm i et vanlig norsk hjem. Skriv svaret som én kort, orginal og engasjerende setning.",

                        ),
                    )
                )
            )
        }
        val result = response.body<ChatResponse>()
        val reply = result.choices.firstOrNull()?.message?.content
            ?: "Viste du at én Grandiosa trenger ca. 1 kWt med strøm for å stekes i en vanlig ovn?"
        return reply
    }
}
package no.uio.ifi.in2000.cellmate.domain.model.openai

data class ChatRequest(
    val model: String,
    val messages: List<Message>
)
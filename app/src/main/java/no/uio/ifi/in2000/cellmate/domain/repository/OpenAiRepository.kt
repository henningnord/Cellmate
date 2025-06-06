package no.uio.ifi.in2000.cellmate.domain.repository

interface OpenAiRepository {
    suspend fun generateFunFact(prompt: String): String
}
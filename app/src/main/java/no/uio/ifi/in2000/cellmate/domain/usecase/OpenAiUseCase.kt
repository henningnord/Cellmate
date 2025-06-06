package no.uio.ifi.in2000.cellmate.domain.usecase

import no.uio.ifi.in2000.cellmate.domain.repository.OpenAiRepository
import javax.inject.Inject

class OpenAiUseCase @Inject constructor (
    private val openAiRepository: OpenAiRepository
) {
    suspend fun generateFunFact(prompt: String): String {
        return openAiRepository.generateFunFact(prompt)
    }
}
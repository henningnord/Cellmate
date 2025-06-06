package no.uio.ifi.in2000.cellmate.data.repositoryImpl

import no.uio.ifi.in2000.cellmate.data.remote.OpenAiDataSource
import no.uio.ifi.in2000.cellmate.domain.repository.OpenAiRepository
import javax.inject.Inject

class OpenAiRepositoryImpl @Inject constructor(
    private val dataSource: OpenAiDataSource) : OpenAiRepository {
    override suspend fun generateFunFact(prompt: String): String {
        return dataSource.getFunFact(prompt)
    }
}
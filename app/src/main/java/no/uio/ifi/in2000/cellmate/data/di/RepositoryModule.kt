package no.uio.ifi.in2000.cellmate.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import no.uio.ifi.in2000.cellmate.data.repositoryImpl.AreaIdRepositoryImpl
import no.uio.ifi.in2000.cellmate.data.repositoryImpl.SavedHomeRepositoryImpl
import no.uio.ifi.in2000.cellmate.data.repositoryImpl.ExpectedUsageRepositoryImpl
import no.uio.ifi.in2000.cellmate.data.repositoryImpl.FrostRepositoryImpl
import no.uio.ifi.in2000.cellmate.data.repositoryImpl.MapBoxRepositoryImpl
import no.uio.ifi.in2000.cellmate.data.repositoryImpl.OpenAiRepositoryImpl
import no.uio.ifi.in2000.cellmate.data.repositoryImpl.PriceRepositoryImpl
import no.uio.ifi.in2000.cellmate.data.repositoryImpl.SolarRepositoryImpl
import no.uio.ifi.in2000.cellmate.domain.repository.AreaIdRepository
import no.uio.ifi.in2000.cellmate.domain.repository.ExpectedUsageRepository
import no.uio.ifi.in2000.cellmate.domain.repository.FrostRepository
import no.uio.ifi.in2000.cellmate.domain.repository.MapBoxRepository
import no.uio.ifi.in2000.cellmate.domain.repository.OpenAiRepository
import no.uio.ifi.in2000.cellmate.domain.repository.PriceRepository
import no.uio.ifi.in2000.cellmate.domain.repository.SavedHomeRepository
import no.uio.ifi.in2000.cellmate.domain.repository.SolarRepository
import javax.inject.Singleton

// The module is responsible for binding repository implementations to their interfaces.
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFrostRepository(
        impl: FrostRepositoryImpl
    ): FrostRepository

    @Binds
    @Singleton
    abstract fun bindMapboxRepository(
        impl: MapBoxRepositoryImpl
    ): MapBoxRepository

    @Binds
    @Singleton
    abstract fun bindSolarRepository(
        impl: SolarRepositoryImpl
    ): SolarRepository

    @Binds
    @Singleton
    abstract fun bindOpenAiRepository(
        impl: OpenAiRepositoryImpl
    ): OpenAiRepository

    @Binds
    @Singleton
    abstract fun bindPriceRepository(
        impl: PriceRepositoryImpl
    ): PriceRepository

    @Binds
    @Singleton
    abstract fun bindExpectedUsageRepository(
        impl: ExpectedUsageRepositoryImpl
    ): ExpectedUsageRepository

    @Binds
    abstract fun bindSavedHomeRepository(
        savedHomeRepositoryImpl: SavedHomeRepositoryImpl
    ): SavedHomeRepository

    @Binds
    @Singleton
    abstract fun bindAreaIdRepository(
        areaIdRepositoryImpl: AreaIdRepositoryImpl
    ): AreaIdRepository
}
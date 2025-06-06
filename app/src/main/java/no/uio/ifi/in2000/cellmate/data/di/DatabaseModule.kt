package no.uio.ifi.in2000.cellmate.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import no.uio.ifi.in2000.cellmate.data.local.database.AppDatabase
import no.uio.ifi.in2000.cellmate.data.local.database.dao.ExpectedUsageDao
import no.uio.ifi.in2000.cellmate.data.local.database.dao.FrostDao
import no.uio.ifi.in2000.cellmate.data.local.database.dao.SavedHomeDao
import no.uio.ifi.in2000.cellmate.data.local.database.dao.SolarDao
import javax.inject.Singleton

// This module provides the database and DAO instances for the dependency injection via Hilt.
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun provideFrostDao(db: AppDatabase): FrostDao = db.frostDao()

    @Provides
    fun provideSolarDao(db: AppDatabase): SolarDao = db.solarDao()


    @Provides
    fun provideExpectedUsageDao(db: AppDatabase): ExpectedUsageDao = db.expectedUsageDao()

    @Provides
    fun provideSavedHomeDao(database: AppDatabase): SavedHomeDao = database.savedHomeDao()
}
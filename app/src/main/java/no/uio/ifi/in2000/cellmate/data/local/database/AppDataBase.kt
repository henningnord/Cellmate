package no.uio.ifi.in2000.cellmate.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import no.uio.ifi.in2000.cellmate.data.local.database.dao.ExpectedUsageDao
import no.uio.ifi.in2000.cellmate.data.local.database.dao.FrostDao
import no.uio.ifi.in2000.cellmate.data.local.database.dao.SolarDao
import no.uio.ifi.in2000.cellmate.data.local.database.dao.SavedHomeDao
import no.uio.ifi.in2000.cellmate.data.local.database.entity.ExpectedUsageEntity
import no.uio.ifi.in2000.cellmate.data.local.database.entity.FrostCacheEntity
import no.uio.ifi.in2000.cellmate.data.local.database.entity.FrostObservationEntity
import no.uio.ifi.in2000.cellmate.data.local.database.entity.FrostObservationDataEntity
import no.uio.ifi.in2000.cellmate.data.local.database.entity.SavedHomeEntity
import no.uio.ifi.in2000.cellmate.data.local.database.entity.SolarCacheEntity

@Database(
    entities = [
        FrostCacheEntity::class,
        FrostObservationEntity::class,
        FrostObservationDataEntity::class,
        SolarCacheEntity::class,
        ExpectedUsageEntity::class,
        SavedHomeEntity::class

    ],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun frostDao(): FrostDao
    abstract fun solarDao(): SolarDao
    abstract fun expectedUsageDao(): ExpectedUsageDao
    abstract fun savedHomeDao(): SavedHomeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
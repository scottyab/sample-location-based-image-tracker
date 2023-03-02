package com.scottyab.challenge.data.datasource.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [SnapshotDb::class, ActivityDb::class, CurrentActivityDb::class],
    // increment this any time we have DB changes.
    version = 2,
    exportSchema = false
)
@TypeConverters(LocalDateTimeConverter::class)
abstract class TrackerDatabase : RoomDatabase() {

    abstract fun snapshotsDao(): SnapshotDao

    abstract fun activityDao(): ActivityDao

    companion object {

        @Volatile
        internal var INSTANCE: TrackerDatabase? = null

        fun getInstance(context: Context): TrackerDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
        }

        private const val DATABASE_NAME = "trackerDB"

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            TrackerDatabase::class.java,
            DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }
}

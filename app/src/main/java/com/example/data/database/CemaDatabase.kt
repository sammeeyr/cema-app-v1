package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        BookmarkEntity::class,
        NoteEntity::class,
        HighlightEntity::class,
        ReadingProgressEntity::class,
        GivingRecordEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CemaDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun noteDao(): NoteDao
    abstract fun highlightDao(): HighlightDao
    abstract fun readingProgressDao(): ReadingProgressDao
    abstract fun givingRecordDao(): GivingRecordDao

    companion object {
        @Volatile
        private var INSTANCE: CemaDatabase? = null

        fun getDatabase(context: Context): CemaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CemaDatabase::class.java,
                    "cema_app_database"
                )
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

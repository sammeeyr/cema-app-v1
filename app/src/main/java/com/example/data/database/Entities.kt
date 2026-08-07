package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val book: String,
    val chapter: Int,
    val verse: Int,
    val translation: String = "KJV",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val book: String,
    val chapter: Int,
    val verse: Int,
    val verseText: String = "",
    val noteText: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "highlights")
data class HighlightEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val book: String,
    val chapter: Int,
    val verse: Int,
    val colorHex: String, // #FEF08A (Yellow), #BAE6FD (Blue), #FBCFE8 (Pink), #BBF7D0 (Green)
    val verseText: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "reading_progress")
data class ReadingProgressEntity(
    @PrimaryKey val lessonId: String,
    val progressPercent: Int,
    val lastReadTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "giving_records")
data class GivingRecordEntity(
    @PrimaryKey val id: String,
    val type: String, // Tithes, Offering, Project, Partnership
    val amount: Double,
    val status: String, // Completed, Pending
    val reference: String,
    val paymentMethod: String,
    val timestamp: Long = System.currentTimeMillis()
)

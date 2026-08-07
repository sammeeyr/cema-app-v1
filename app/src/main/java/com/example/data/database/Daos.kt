package com.example.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY timestamp DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE book = :book AND chapter = :chapter AND verse = :verse")
    suspend fun deleteBookmark(book: String, chapter: Int, verse: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE book = :book AND chapter = :chapter AND verse = :verse)")
    suspend fun isBookmarked(book: String, chapter: Int, verse: Int): Boolean
}

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNoteById(id: Int)
}

@Dao
interface HighlightDao {
    @Query("SELECT * FROM highlights ORDER BY timestamp DESC")
    fun getAllHighlights(): Flow<List<HighlightEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHighlight(highlight: HighlightEntity)

    @Query("DELETE FROM highlights WHERE book = :book AND chapter = :chapter AND verse = :verse")
    suspend fun deleteHighlight(book: String, chapter: Int, verse: Int)

    @Query("SELECT * FROM highlights WHERE book = :book AND chapter = :chapter")
    fun getHighlightsForChapter(book: String, chapter: Int): Flow<List<HighlightEntity>>
}

@Dao
interface ReadingProgressDao {
    @Query("SELECT * FROM reading_progress")
    fun getAllProgress(): Flow<List<ReadingProgressEntity>>

    @Query("SELECT * FROM reading_progress WHERE lessonId = :lessonId LIMIT 1")
    suspend fun getProgressForLesson(lessonId: String): ReadingProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateProgress(progress: ReadingProgressEntity)
}

@Dao
interface GivingRecordDao {
    @Query("SELECT * FROM giving_records ORDER BY timestamp DESC")
    fun getAllGivingRecords(): Flow<List<GivingRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGivingRecord(record: GivingRecordEntity)
}

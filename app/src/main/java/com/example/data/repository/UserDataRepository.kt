package com.example.data.repository

import com.example.data.database.*
import kotlinx.coroutines.flow.Flow

class UserDataRepository(
    private val bookmarkDao: BookmarkDao,
    private val noteDao: NoteDao,
    private val highlightDao: HighlightDao,
    private val readingProgressDao: ReadingProgressDao,
    private val givingRecordDao: GivingRecordDao
) {

    val allBookmarks: Flow<List<BookmarkEntity>> = bookmarkDao.getAllBookmarks()
    val allNotes: Flow<List<NoteEntity>> = noteDao.getAllNotes()
    val allHighlights: Flow<List<HighlightEntity>> = highlightDao.getAllHighlights()
    val allReadingProgress: Flow<List<ReadingProgressEntity>> = readingProgressDao.getAllProgress()
    val allGivingRecords: Flow<List<GivingRecordEntity>> = givingRecordDao.getAllGivingRecords()

    suspend fun addBookmark(book: String, chapter: Int, verse: Int, text: String, translation: String) {
        bookmarkDao.insertBookmark(
            BookmarkEntity(
                book = book,
                chapter = chapter,
                verse = verse,
                translation = translation,
                text = text
            )
        )
    }

    suspend fun removeBookmark(book: String, chapter: Int, verse: Int) {
        bookmarkDao.deleteBookmark(book, chapter, verse)
    }

    suspend fun isBookmarked(book: String, chapter: Int, verse: Int): Boolean {
        return bookmarkDao.isBookmarked(book, chapter, verse)
    }

    suspend fun addNote(book: String, chapter: Int, verse: Int, verseText: String, noteText: String) {
        noteDao.insertNote(
            NoteEntity(
                book = book,
                chapter = chapter,
                verse = verse,
                verseText = verseText,
                noteText = noteText
            )
        )
    }

    suspend fun deleteNote(id: Int) {
        noteDao.deleteNoteById(id)
    }

    suspend fun addHighlight(book: String, chapter: Int, verse: Int, colorHex: String, verseText: String) {
        highlightDao.insertHighlight(
            HighlightEntity(
                book = book,
                chapter = chapter,
                verse = verse,
                colorHex = colorHex,
                verseText = verseText
            )
        )
    }

    suspend fun removeHighlight(book: String, chapter: Int, verse: Int) {
        highlightDao.deleteHighlight(book, chapter, verse)
    }

    fun getHighlightsForChapter(book: String, chapter: Int): Flow<List<HighlightEntity>> {
        return highlightDao.getHighlightsForChapter(book, chapter)
    }

    suspend fun updateLessonProgress(lessonId: String, percent: Int) {
        readingProgressDao.updateProgress(
            ReadingProgressEntity(
                lessonId = lessonId,
                progressPercent = percent
            )
        )
    }

    suspend fun recordGiving(type: String, amount: Double, method: String, reference: String) {
        givingRecordDao.insertGivingRecord(
            GivingRecordEntity(
                id = "GIV_" + System.currentTimeMillis(),
                type = type,
                amount = amount,
                status = "Completed",
                reference = reference,
                paymentMethod = method
            )
        )
    }
}

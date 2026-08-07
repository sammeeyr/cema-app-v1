package com.example.data.model

data class BibleVerse(
    val book: String,
    val chapter: Int,
    val verse: Int,
    val text: String,
    val translation: String = "KJV"
)

data class BibleBook(
    val name: String,
    val category: String, // Old Testament, New Testament
    val chapterCount: Int
)

data class VersionComparison(
    val version: String,
    val text: String
)

data class VerseSearchResult(
    val book: String,
    val chapter: Int,
    val verse: Int,
    val text: String,
    val translation: String
)

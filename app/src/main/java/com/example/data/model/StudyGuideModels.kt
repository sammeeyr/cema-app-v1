package com.example.data.model

data class StudyGuide(
    val id: String,
    val title: String,
    val subtitle: String,
    val author: String = "Andrew Wommack & Don W. Krow",
    val lessons: List<StudyLesson>
)

data class StudyLesson(
    val id: String,
    val lessonNumber: Int,
    val level: Int = 1,
    val title: String,
    val author: String = "Andrew Wommack",
    val readPassage: String, // e.g. "John 3:1-21"
    val memoryVerse: String, // e.g. "John 3:16"
    val memoryVerseText: String,
    val paragraphs: List<String>,
    val questions: List<String>,
    val reflection: String,
    val prayer: String
)


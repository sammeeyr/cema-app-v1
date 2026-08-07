package com.example.ui.viewmodel

import android.app.Application
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.CemaDatabase
import com.example.data.database.HighlightEntity
import com.example.data.database.NoteEntity
import com.example.data.database.BookmarkEntity
import com.example.data.database.GivingRecordEntity
import com.example.data.model.*
import com.example.data.repository.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineExceptionHandler
import java.util.Locale

enum class CemaTab {
    HOME, BIBLE, STUDY, NOTEBOOK, GIVE, AI_ASSISTANT, PROFILE, SETTINGS, SERMONS, ANNOUNCEMENTS
}

enum class ReadingMode {
    PAPER, SEPIA, DARK
}

data class AiChatMessage(
    val id: String = System.currentTimeMillis().toString(),
    val sender: String, // "user" or "ai"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

class CemaViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {

    private val db = CemaDatabase.getDatabase(application)
    val bibleRepository = BibleRepository()
    val studyRepository = StudyRepository()
    val geminiAiRepository = GeminiAiRepository()
    val userDataRepository = UserDataRepository(
        db.bookmarkDao(),
        db.noteDao(),
        db.highlightDao(),
        db.readingProgressDao(),
        db.givingRecordDao()
    )

    // Text To Speech Engine
    private var tts: TextToSpeech? = null
    private val _isTtsReady = MutableStateFlow(false)
    val isTtsReady: StateFlow<Boolean> = _isTtsReady.asStateFlow()

    // Global Error State
    val errorMessage = MutableStateFlow<String?>(null)

    fun clearError() {
        errorMessage.value = null
    }

    fun showError(message: String) {
        errorMessage.value = message
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        val msg = exception.localizedMessage ?: "An unexpected error occurred"
        showError(msg)
    }

    init {
        tts = TextToSpeech(application, this)
        seedSampleDataIfEmpty()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US
            _isTtsReady.value = true
        }
    }

    fun speakText(text: String) {
        if (_isTtsReady.value) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "cema_tts_${System.currentTimeMillis()}")
        }
    }

    fun stopSpeaking() {
        tts?.stop()
    }

    override fun onCleared() {
        super.onCleared()
        tts?.stop()
        tts?.shutdown()
    }

    private fun seedSampleDataIfEmpty() {
        viewModelScope.launch(exceptionHandler) {
            // Seed sample giving records if none exist
            userDataRepository.allGivingRecords.firstOrNull()?.let { list ->
                if (list.isEmpty()) {
                    userDataRepository.recordGiving("Offering", 5000.0, "Paystack", "PST_9823412")
                    userDataRepository.recordGiving("Tithes", 25000.0, "Bank Transfer", "TRF_7723019")
                }
            }

            // Seed initial highlights & notes
            userDataRepository.allHighlights.firstOrNull()?.let { list ->
                if (list.isEmpty()) {
                    userDataRepository.addHighlight("John", 3, 16, "#FEF08A", "For God so loved the world...")
                    userDataRepository.addHighlight("Romans", 8, 28, "#BAE6FD", "And we know that all things work together for good...")
                }
            }

            userDataRepository.allBookmarks.firstOrNull()?.let { list ->
                if (list.isEmpty()) {
                    userDataRepository.addBookmark("John", 3, 16, "For God so loved the world...", "KJV")
                    userDataRepository.addBookmark("Romans", 5, 1, "Therefore being justified by faith...", "NKJV")
                }
            }
        }
    }

    // --- Navigation ---
    private val _selectedTab = MutableStateFlow(CemaTab.HOME)
    val selectedTab: StateFlow<CemaTab> = _selectedTab.asStateFlow()

    fun selectTab(tab: CemaTab) {
        _selectedTab.value = tab
    }

    // --- User Profile State ---
    val isUserSignedIn = MutableStateFlow(false)
    val userName = MutableStateFlow("Guest")
    val userEmail = MutableStateFlow("")
    val userPhone = MutableStateFlow("+234 812 345 6789")
    val churchUnit = MutableStateFlow("Media & Worship Ministry")
    val userLocation = MutableStateFlow("Lagos, Nigeria")

    // --- Settings State ---
    val selectedBibleVersion = MutableStateFlow("KJV")
    val fontSizeSp = MutableStateFlow(18)
    val readingMode = MutableStateFlow(ReadingMode.PAPER)
    val isDarkMode = MutableStateFlow(false)
    val notificationsEnabled = MutableStateFlow(true)

    // --- Bible Reader State ---
    val selectedBook = MutableStateFlow("John")
    val selectedChapter = MutableStateFlow(3)
    val searchQuery = MutableStateFlow("")
    
    private val _currentVerses = MutableStateFlow<List<BibleVerse>>(emptyList())
    val currentVerses: StateFlow<List<BibleVerse>> = _currentVerses.asStateFlow()

    private val _searchResults = MutableStateFlow<List<VerseSearchResult>>(emptyList())
    val searchResults: StateFlow<List<VerseSearchResult>> = _searchResults.asStateFlow()

    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches: StateFlow<List<String>> = _recentSearches.asStateFlow()

    val selectedVerseForBottomSheet = MutableStateFlow<BibleVerse?>(null)
    val comparisonVerses = MutableStateFlow<List<VersionComparison>>(emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val chapterHighlights: StateFlow<List<HighlightEntity>> = selectedBook
        .combine(selectedChapter) { book, ch -> Pair(book, ch) }
        .flatMapLatest { (book, ch) ->
            userDataRepository.getHighlightsForChapter(book, ch)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadVerses()
    }

    fun selectBookAndChapter(book: String, chapter: Int) {
        selectedBook.value = book
        selectedChapter.value = chapter
        loadVerses()
    }

    fun setBibleVersion(version: String) {
        selectedBibleVersion.value = version
        loadVerses()
    }

    fun loadVerses() {
        viewModelScope.launch(exceptionHandler) {
            _currentVerses.value = bibleRepository.getVerses(
                selectedBook.value,
                selectedChapter.value,
                selectedBibleVersion.value
            )
        }
    }

    fun searchBible(query: String) {
        searchQuery.value = query
        viewModelScope.launch(exceptionHandler) {
            _searchResults.value = bibleRepository.searchVerses(query, selectedBibleVersion.value)
        }
    }

    fun submitSearch(query: String) {
        if (query.isNotBlank()) {
            val history = _recentSearches.value.toMutableList()
            history.remove(query)
            history.add(0, query)
            if (history.size > 5) {
                history.removeAt(history.lastIndex)
            }
            _recentSearches.value = history
        }
        searchBible(query)
    }

    fun removeRecentSearch(query: String) {
        val history = _recentSearches.value.toMutableList()
        history.remove(query)
        _recentSearches.value = history
    }

    
    fun signInWithGoogle(context: android.content.Context) {
        viewModelScope.launch {
            val authManager = com.example.ui.AuthManager(context)
            val idToken = authManager.signInWithGoogle()
            if (idToken != null) {
                // Usually we authenticate with Firebase here
                isUserSignedIn.value = true
                userName.value = "User"
                userEmail.value = "user@gmail.com"
            } else {
                errorMessage.value = "Failed to sign in"
            }
        }
    }

    fun clearRecentSearches() {
        _recentSearches.value = emptyList()
    }

    fun openVerseBottomSheet(verse: BibleVerse) {
        selectedVerseForBottomSheet.value = verse
        viewModelScope.launch(exceptionHandler) {
            comparisonVerses.value = bibleRepository.compareVerse(verse.book, verse.chapter, verse.verse, verse.text)
        }
    }

    fun closeVerseBottomSheet() {
        selectedVerseForBottomSheet.value = null
    }

    // Bottom Sheet Verse Actions
    fun addVerseHighlight(colorHex: String) {
        val verse = selectedVerseForBottomSheet.value ?: return
        viewModelScope.launch(exceptionHandler) {
            userDataRepository.addHighlight(verse.book, verse.chapter, verse.verse, colorHex, verse.text)
        }
    }

    fun addVerseBookmark(verseParam: com.example.data.model.BibleVerse? = null) {
        val verse = verseParam ?: selectedVerseForBottomSheet.value ?: return
        viewModelScope.launch(exceptionHandler) {
            userDataRepository.addBookmark(verse.book, verse.chapter, verse.verse, verse.text, verse.translation)
        }
    }

    fun addVerseNote(noteText: String) {
        val verse = selectedVerseForBottomSheet.value ?: return
        viewModelScope.launch(exceptionHandler) {
            userDataRepository.addNote(verse.book, verse.chapter, verse.verse, verse.text, noteText)
        }
    }

    fun removeBookmark(book: String, chapter: Int, verse: Int) {
        viewModelScope.launch(exceptionHandler) {
            userDataRepository.removeBookmark(book, chapter, verse)
        }
    }

    fun deleteNote(noteId: Int) {
        viewModelScope.launch(exceptionHandler) {
            userDataRepository.deleteNote(noteId)
        }
    }

    // --- Study Guide State ---
    val currentLessonId = MutableStateFlow("lesson_1")
    val selectedLesson = currentLessonId.map { id ->
        studyRepository.getLessonById(id) ?: studyRepository.defaultStudyGuide.lessons.first()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), studyRepository.defaultStudyGuide.lessons.first())

    val readingProgressList = userDataRepository.allReadingProgress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateLessonProgress(lessonId: String, percent: Int) {
        viewModelScope.launch(exceptionHandler) {
            userDataRepository.updateLessonProgress(lessonId, percent)
        }
    }

    // --- User Notebook Flows ---
    val bookmarksList: StateFlow<List<BookmarkEntity>> = userDataRepository.allBookmarks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notesList: StateFlow<List<NoteEntity>> = userDataRepository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val highlightsList: StateFlow<List<HighlightEntity>> = userDataRepository.allHighlights
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Giving State ---
    val givingType = MutableStateFlow("Offering")
    val givingAmount = MutableStateFlow("5000")
    val givingPaymentMethod = MutableStateFlow("Paystack") // "Paystack" or "Bank Transfer"
    val givingSuccessMessage = MutableStateFlow<String?>(null)

    val givingRecordsList: StateFlow<List<GivingRecordEntity>> = userDataRepository.allGivingRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun processGiving(amount: Double) {
        viewModelScope.launch(exceptionHandler) {
            val ref = if (givingPaymentMethod.value == "Paystack") "PST_${System.currentTimeMillis().toString().takeLast(7)}" else "TRF_${System.currentTimeMillis().toString().takeLast(7)}"
            userDataRepository.recordGiving(givingType.value, amount, givingPaymentMethod.value, ref)
            givingSuccessMessage.value = "Payment of ₦${String.format("%,.0f", amount)} for ${givingType.value} completed via ${givingPaymentMethod.value}! Ref: $ref"
        }
    }

    // --- AI Study Companion State ---
    private val _aiChatMessages = MutableStateFlow(
        listOf(
            AiChatMessage(
                sender = "ai",
                text = "Grace and peace, Samuel! I am your CEMA AI Study Companion. How can I help you study God's Word today?"
            )
        )
    )
    val aiChatMessages: StateFlow<List<AiChatMessage>> = _aiChatMessages.asStateFlow()

    val isAiLoading = MutableStateFlow(false)

    fun sendAiPrompt(userPrompt: String) {
        if (userPrompt.isBlank()) return
        val userMsg = AiChatMessage(sender = "user", text = userPrompt)
        _aiChatMessages.value = _aiChatMessages.value + userMsg
        isAiLoading.value = true

        viewModelScope.launch(exceptionHandler) {
            try {
                val aiResponse = geminiAiRepository.askGemini(userPrompt)
                val aiMsg = AiChatMessage(sender = "ai", text = aiResponse)
                _aiChatMessages.value = _aiChatMessages.value + aiMsg
            } catch (e: Exception) {
                showError("AI error: ${e.localizedMessage}")
            } finally {
                isAiLoading.value = false
            }
        }
    }

    fun explainVerseWithAi(verseText: String) {
        selectTab(CemaTab.AI_ASSISTANT)
        sendAiPrompt("Please give a clear, inspiring scriptural explanation and practical application for this verse: \"$verseText\"")
    }

    fun askAiQuestion(question: String) {
        selectTab(CemaTab.AI_ASSISTANT)
        sendAiPrompt(question)
    }
}

package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BibleBook
import com.example.data.model.BibleVerse
import com.example.ui.theme.*
import com.example.ui.viewmodel.CemaViewModel

private fun highlightText(text: String, query: String, color: Color): AnnotatedString {
    if (query.isBlank()) return AnnotatedString(text)
    return buildAnnotatedString {
        var start = 0
        while (start < text.length) {
            val idx = text.indexOf(query, start, ignoreCase = true)
            if (idx == -1) {
                append(text.substring(start))
                break
            }
            append(text.substring(start, idx))
            withStyle(style = SpanStyle(background = color)) {
                append(text.substring(idx, idx + query.length))
            }
            start = idx + query.length
        }
    }
}

enum class ReaderThemeMode {
    LIGHT, SEPIA, DARK
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BibleScreen(
    viewModel: CemaViewModel
) {
    val context = LocalContext.current
    val verses by viewModel.currentVerses.collectAsState()
    val selectedBook by viewModel.selectedBook.collectAsState()
    val selectedChapter by viewModel.selectedChapter.collectAsState()
    val selectedVersion by viewModel.selectedBibleVersion.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val chapterHighlights by viewModel.chapterHighlights.collectAsState()
    val selectedVerseForSheet by viewModel.selectedVerseForBottomSheet.collectAsState()
    val comparisonVerses by viewModel.comparisonVerses.collectAsState()
    val bookmarks by viewModel.bookmarksList.collectAsState()
    val isTtsReady by viewModel.isTtsReady.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState()

    // YouVersion Reading Settings State
    var readerFontSize by remember { mutableStateOf(18) }
    var readerTheme by remember { mutableStateOf(ReaderThemeMode.LIGHT) }
    var readerFontFamily by remember { mutableStateOf(FontFamily.Serif) }

    // Dialog & Picker States
    var showBookPickerSheet by remember { mutableStateOf(false) }
    var showVersionPickerSheet by remember { mutableStateOf(false) }
    var showReaderSettingsSheet by remember { mutableStateOf(false) }
    var showNoteDialog by remember { mutableStateOf(false) }
    var verseToShare by remember { mutableStateOf<BibleVerse?>(null) }
    var noteTextInput by remember { mutableStateOf("") }
    var isSearchFocused by remember { mutableStateOf(false) }
    var isPlayingAudio by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    val highlightColorMap = remember(chapterHighlights) {
        chapterHighlights.associateBy { it.verse }
    }

    BackHandler(enabled = isSearchFocused || searchQuery.isNotEmpty()) {
        viewModel.searchBible("")
        focusManager.clearFocus()
    }

    // Reader background colors based on ReaderThemeMode
    val readerBgColor = when (readerTheme) {
        ReaderThemeMode.LIGHT -> MaterialTheme.colorScheme.background
        ReaderThemeMode.SEPIA -> Color(0xFFFBF0D9)
        ReaderThemeMode.DARK -> Color(0xFF18181B)
    }
    val readerTextColor = when (readerTheme) {
        ReaderThemeMode.LIGHT -> MaterialTheme.colorScheme.onBackground
        ReaderThemeMode.SEPIA -> Color(0xFF4A3B32)
        ReaderThemeMode.DARK -> Color(0xFFE4E4E7)
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // YouVersion Top Toolbar Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. Book & Chapter Selector Pill (e.g. Genesis 1 ▼)
                    Surface(
                        onClick = { showBookPickerSheet = true },
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        tonalElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.MenuBook,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$selectedBook $selectedChapter",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 2. Version Selector Chip
                        Surface(
                            onClick = { showVersionPickerSheet = true },
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedVersion,
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        // 3. Audio Listen Button
                        IconButton(
                            onClick = {
                                if (isPlayingAudio) {
                                    viewModel.stopSpeaking()
                                    isPlayingAudio = false
                                } else {
                                    if (isTtsReady && verses.isNotEmpty()) {
                                        val fullText = "$selectedBook chapter $selectedChapter. " + verses.joinToString(". ") { it.text }
                                        viewModel.speakText(fullText)
                                        isPlayingAudio = true
                                        Toast.makeText(context, "Playing $selectedBook $selectedChapter", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Text-to-Speech initialising...", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isPlayingAudio) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Outlined.VolumeUp,
                                contentDescription = "Audio Reader",
                                tint = if (isPlayingAudio) CemaFlameOrange else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // 4. Reader Display Settings Button (aA)
                        IconButton(onClick = { showReaderSettingsSheet = true }) {
                            Text(
                                text = "aA",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.searchBible(it) },
                    placeholder = { Text("Search verses e.g. Faith, Grace, Love...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.searchBible(""); focusManager.clearFocus() }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .onFocusChanged { state -> isSearchFocused = state.isFocused },
                    shape = RoundedCornerShape(24.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { 
                        viewModel.submitSearch(searchQuery)
                        focusManager.clearFocus()
                    })
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(readerBgColor)
        ) {
            if (searchQuery.isNotBlank()) {
                // Search Results View
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = "Search Results for \"$searchQuery\" (${searchResults.size})",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    items(searchResults) { result ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.submitSearch(searchQuery)
                                    viewModel.selectBookAndChapter(result.book, result.chapter)
                                    viewModel.searchBible("")
                                    focusManager.clearFocus()
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "${result.book} ${result.chapter}:${result.verse} (${result.translation})",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = highlightText(
                                        text = result.text,
                                        query = searchQuery,
                                        color = HighlightYellow.copy(alpha = 0.7f)
                                    ),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = readerFontFamily),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            } else if (isSearchFocused) {
                // Recent Searches
                if (recentSearches.isNotEmpty()) {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Recent Searches",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                TextButton(onClick = { viewModel.clearRecentSearches() }) {
                                    Text("Clear")
                                }
                            }
                        }
                        items(recentSearches) { recentQuery ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.searchBible(recentQuery)
                                        viewModel.submitSearch(recentQuery)
                                        focusManager.clearFocus()
                                    }
                                    .padding(vertical = 12.dp, horizontal = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.History,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = recentQuery,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.removeRecentSearch(recentQuery) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No recent searches",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                // YouVersion Scripture Reader View
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Verse of the Day Header Card (YouVersion Style)
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = CemaPrimary.copy(alpha = 0.12f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Outlined.AutoAwesome,
                                            contentDescription = null,
                                            tint = CemaFlameOrange,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Verse of the Day",
                                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                            color = CemaPrimary
                                        )
                                    }
                                    Text(
                                        text = "Philippians 4:13",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = CemaPrimary
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "“I can do all things through Christ who strengthens me.”",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = readerTextColor
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(
                                        onClick = {
                                            verseToShare = BibleVerse(
                                                book = "Philippians",
                                                chapter = 4,
                                                verse = 13,
                                                text = "I can do all things through Christ who strengthens me.",
                                                translation = selectedVersion
                                            )
                                        },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Share,
                                            contentDescription = "Share Verse",
                                            modifier = Modifier.size(16.dp),
                                            tint = CemaPrimary
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Share Graphic/Text",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = CemaPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Chapter Title Header
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$selectedBook $selectedChapter",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = readerFontFamily
                                ),
                                color = readerTextColor
                            )

                            Text(
                                text = selectedVersion,
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    }

                    // Verses List
                    items(verses) { verse ->
                        val highlight = highlightColorMap[verse.verse]
                        val bg = when (highlight?.colorHex) {
                            "#FEF08A" -> HighlightYellow.copy(alpha = 0.6f)
                            "#BAE6FD" -> HighlightBlue.copy(alpha = 0.6f)
                            "#FBCFE8" -> HighlightPink.copy(alpha = 0.6f)
                            "#BBF7D0" -> HighlightGreen.copy(alpha = 0.6f)
                            else -> Color.Transparent
                        }

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    viewModel.openVerseBottomSheet(verse)
                                },
                            color = bg
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = "${verse.verse} ",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                                Text(
                                    text = verse.text,
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontFamily = readerFontFamily,
                                        lineHeight = (readerFontSize * 1.5).sp,
                                        fontSize = readerFontSize.sp
                                    ),
                                    color = readerTextColor
                                )
                                val isVerseBookmarked = bookmarks.any { it.book == verse.book && it.chapter == verse.chapter && it.verse == verse.verse }
                                if (isVerseBookmarked) {
                                    Icon(
                                        imageVector = Icons.Filled.Bookmark,
                                        contentDescription = "Bookmarked",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Chapter Bottom Navigation Bar (Previous Chapter / Next Chapter)
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.previousChapter() },
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Previous")
                            }

                            Button(
                                onClick = { viewModel.nextChapter() },
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text("Next Chapter")
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }

    // --- YouVersion Modern Book & Chapter Picker Sheet ---
    if (showBookPickerSheet) {
        var activePickerTab by remember { mutableStateOf(0) } // 0: Books, 1: Chapters
        var pickerBookSearch by remember { mutableStateOf("") }
        var testementFilter by remember { mutableStateOf("All") } // All, OT, NT

        val filteredBooks = remember(pickerBookSearch, testementFilter) {
            viewModel.bibleRepository.books.filter { b ->
                val matchesSearch = b.name.contains(pickerBookSearch, ignoreCase = true)
                val matchesTestament = when (testementFilter) {
                    "OT" -> b.category == "Old Testament"
                    "NT" -> b.category == "New Testament"
                    else -> true
                }
                matchesSearch && matchesTestament
            }
        }

        ModalBottomSheet(
            onDismissRequest = { showBookPickerSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 620.dp)
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                // Sheet Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Select Scripture",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = { showBookPickerSheet = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Picker Tabs (Books / Chapters)
                TabRow(
                    selectedTabIndex = activePickerTab,
                    containerColor = Color.Transparent
                ) {
                    Tab(
                        selected = activePickerTab == 0,
                        onClick = { activePickerTab = 0 },
                        text = { Text("1. Book", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = activePickerTab == 1,
                        onClick = { activePickerTab = 1 },
                        text = { Text("2. Chapter", fontWeight = FontWeight.Bold) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (activePickerTab == 0) {
                    // Search & Testament Filters for Books
                    OutlinedTextField(
                        value = pickerBookSearch,
                        onValueChange = { pickerBookSearch = it },
                        placeholder = { Text("Search 66 Books (e.g. Psalms, John)...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(20.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = (testementFilter == "All"),
                            onClick = { testementFilter = "All" },
                            label = { Text("All 66") }
                        )
                        FilterChip(
                            selected = (testementFilter == "OT"),
                            onClick = { testementFilter = "OT" },
                            label = { Text("Old Testament (39)") }
                        )
                        FilterChip(
                            selected = (testementFilter == "NT"),
                            onClick = { testementFilter = "NT" },
                            label = { Text("New Testament (27)") }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(filteredBooks) { book ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.selectedBook.value = book.name
                                        activePickerTab = 1 // Auto advance to chapter grid
                                    },
                                shape = RoundedCornerShape(12.dp),
                                color = if (book.name == selectedBook) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = book.name,
                                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                                            color = if (book.name == selectedBook) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${book.category} • ${book.chapterCount} Chapters",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Chapters Grid View
                    val maxChapters = viewModel.bibleRepository.books.find { it.name == selectedBook }?.chapterCount ?: 28
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Select Chapter in $selectedBook",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(5),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items((1..maxChapters).toList()) { ch ->
                                Surface(
                                    modifier = Modifier
                                        .aspectRatio(1f)
                                        .clickable {
                                            viewModel.selectedChapter.value = ch
                                            viewModel.loadVerses()
                                            showBookPickerSheet = false
                                        },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (ch == selectedChapter) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "$ch",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = if (ch == selectedChapter) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- Version Picker Sheet ---
    if (showVersionPickerSheet) {
        ModalBottomSheet(onDismissRequest = { showVersionPickerSheet = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Select Bible Version",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))
                val versionsWithDesc = listOf(
                    Pair("NKJV", "New King James Version (Accurate & Readable)"),
                    Pair("KJV", "King James Version (Classic Traditional)"),
                    Pair("NLT", "New Living Translation (Clear & Contemporary)"),
                    Pair("MSG", "The Message Bible (Paraphrase & Expressive)"),
                    Pair("AMP", "Amplified Bible (Expanded Context)"),
                    Pair("TPT", "The Passion Translation (Heart & Emotion)"),
                    Pair("GNT", "Good News Translation (Simple & Direct)"),
                    Pair("NIV", "New International Version (Modern Balance)")
                )
                versionsWithDesc.forEach { (ver, desc) ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.setBibleVersion(ver)
                                showVersionPickerSheet = false
                            }
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = if (ver == selectedVersion) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (ver == selectedVersion),
                                onClick = {
                                    viewModel.setBibleVersion(ver)
                                    showVersionPickerSheet = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = ver,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = desc,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // --- YouVersion Reader Display Settings Sheet ---
    if (showReaderSettingsSheet) {
        ModalBottomSheet(onDismissRequest = { showReaderSettingsSheet = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Display & Font Settings",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Font Size Adjuster
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Font Size", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedButton(
                            onClick = { if (readerFontSize > 14) readerFontSize -= 2 },
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Text("A-", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("${readerFontSize}sp", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(12.dp))
                        OutlinedButton(
                            onClick = { if (readerFontSize < 28) readerFontSize += 2 },
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Text("A+", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Background Theme Picker
                Text("Reading Theme", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FilterChip(
                        selected = (readerTheme == ReaderThemeMode.LIGHT),
                        onClick = { readerTheme = ReaderThemeMode.LIGHT },
                        label = { Text("Light") }
                    )
                    FilterChip(
                        selected = (readerTheme == ReaderThemeMode.SEPIA),
                        onClick = { readerTheme = ReaderThemeMode.SEPIA },
                        label = { Text("Sepia Paper") }
                    )
                    FilterChip(
                        selected = (readerTheme == ReaderThemeMode.DARK),
                        onClick = { readerTheme = ReaderThemeMode.DARK },
                        label = { Text("Night Mode") }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // --- Verse Action Modal Bottom Sheet ---
    if (selectedVerseForSheet != null) {
        val verse = selectedVerseForSheet!!
        ModalBottomSheet(
            onDismissRequest = { viewModel.closeVerseBottomSheet() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${verse.book} ${verse.chapter}:${verse.verse}",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    IconButton(onClick = { viewModel.closeVerseBottomSheet() }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "“${verse.text}”",
                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Serif),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Color Highlight Selector Palette
                Text(
                    text = "Highlight Color",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val colors = listOf(
                        Pair("Yellow", "#FEF08A"),
                        Pair("Sky Blue", "#BAE6FD"),
                        Pair("Soft Pink", "#FBCFE8"),
                        Pair("Mint Green", "#BBF7D0")
                    )
                    colors.forEach { (name, hex) ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    when (hex) {
                                        "#FEF08A" -> HighlightYellow
                                        "#BAE6FD" -> HighlightBlue
                                        "#FBCFE8" -> HighlightPink
                                        else -> HighlightGreen
                                    }
                                )
                                .border(1.dp, Color.Gray, CircleShape)
                                .clickable {
                                    viewModel.addVerseHighlight(hex)
                                    Toast.makeText(context, "Highlighted in $name", Toast.LENGTH_SHORT).show()
                                }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons Toolbar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val isBookmarked = bookmarks.any { it.book == verse.book && it.chapter == verse.chapter && it.verse == verse.verse }
                        IconButton(onClick = {
                            if (isBookmarked) {
                                viewModel.removeBookmark(verse.book, verse.chapter, verse.verse)
                                Toast.makeText(context, "Removed from Bookmarks", Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.addVerseBookmark()
                                Toast.makeText(context, "Saved to Bookmarks", Toast.LENGTH_SHORT).show()
                            }
                        }) {
                            Icon(if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                        Text("Bookmark", style = MaterialTheme.typography.labelSmall)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Verse", "${verse.book} ${verse.chapter}:${verse.verse} - ${verse.text}")
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Copied verse!", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Outlined.ContentCopy, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                        Text("Copy", style = MaterialTheme.typography.labelSmall)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = {
                            verseToShare = verse
                        }) {
                            Icon(Icons.Outlined.Share, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                        Text("Share", style = MaterialTheme.typography.labelSmall)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = { showNoteDialog = true }) {
                            Icon(Icons.AutoMirrored.Outlined.NoteAdd, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                        Text("Add Note", style = MaterialTheme.typography.labelSmall)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = {
                            if (isTtsReady) {
                                viewModel.speakText("${verse.book} chapter ${verse.chapter} verse ${verse.verse}. ${verse.text}")
                            } else {
                                Toast.makeText(context, "Text-to-Speech initialising...", Toast.LENGTH_SHORT).show()
                            }
                        }) {
                            Icon(Icons.AutoMirrored.Outlined.VolumeUp, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                        Text("Read Aloud", style = MaterialTheme.typography.labelSmall)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // AI Explanation Button
                Button(
                    onClick = {
                        val vText = verse.text
                        viewModel.closeVerseBottomSheet()
                        viewModel.explainVerseWithAi(vText)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Outlined.Psychology, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AI Explain Verse & Application")
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Translation Comparison Section
                Text(
                    text = "Compare Translations",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                comparisonVerses.forEach { comp ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = comp.version,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = comp.text,
                                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Serif),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Add Note Dialog
    if (showNoteDialog) {
        AlertDialog(
            onDismissRequest = { showNoteDialog = false },
            title = { Text("Add Personal Reflection Note") },
            text = {
                OutlinedTextField(
                    value = noteTextInput,
                    onValueChange = { noteTextInput = it },
                    placeholder = { Text("Write your reflections, insights, or revelation...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (noteTextInput.isNotBlank()) {
                            viewModel.addVerseNote(noteTextInput)
                            noteTextInput = ""
                            showNoteDialog = false
                            Toast.makeText(context, "Note saved!", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Save Note")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNoteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Verse Share Modal Sheet (Graphic Image & Text Snippet Generator)
    if (verseToShare != null) {
        val vShare = verseToShare!!
        com.example.ui.components.VerseShareDialog(
            verseText = vShare.text,
            reference = "${vShare.book} ${vShare.chapter}:${vShare.verse}",
            version = selectedVersion,
            onDismiss = { verseToShare = null }
        )
    }
}

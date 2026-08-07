package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.withStyle
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    var showBookPicker by remember { mutableStateOf(false) }
    var showNoteDialog by remember { mutableStateOf(false) }
    var noteTextInput by remember { mutableStateOf("") }
    var isSearchFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    val highlightColorMap = remember(chapterHighlights) {
        chapterHighlights.associateBy { it.verse }
    }
    
    BackHandler(enabled = isSearchFocused || searchQuery.isNotEmpty()) {
        viewModel.searchBible("")
        focusManager.clearFocus()
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Book & Chapter & Translation Selectors Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Book & Chapter Button
                    Surface(
                        onClick = { showBookPicker = true },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$selectedBook $selectedChapter",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null
                            )
                        }
                    }

                    // Version Selector Pills
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(viewModel.bibleRepository.availableVersions) { ver ->
                            FilterChip(
                                selected = (ver == selectedVersion),
                                onClick = { viewModel.setBibleVersion(ver) },
                                label = { Text(ver, fontSize = 12.sp) },
                                shape = RoundedCornerShape(20.dp)
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
                .background(MaterialTheme.colorScheme.background)
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
                                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Serif),
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
                // Scripture Reader View
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$selectedBook Chapter $selectedChapter",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif
                                ),
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            Text(
                                text = selectedVersion,
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    }

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
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                                Text(
                                    text = verse.text,
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontFamily = FontFamily.Serif,
                                        lineHeight = 26.sp,
                                        fontSize = 18.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                val isVerseBookmarked = bookmarks.any { it.book == verse.book && it.chapter == verse.chapter && it.verse == verse.verse }
                                if (isVerseBookmarked) {
                                    Icon(Icons.Filled.Bookmark, contentDescription = "Bookmarked", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Book & Chapter Picker Dialog
    if (showBookPicker) {
        AlertDialog(
            onDismissRequest = { showBookPicker = false },
            title = { Text("Select Book & Chapter") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Books",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(viewModel.bibleRepository.books) { book ->
                            FilterChip(
                                selected = (book.name == selectedBook),
                                onClick = { viewModel.selectedBook.value = book.name },
                                label = { Text(book.name) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Chapter",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val maxChapters = viewModel.bibleRepository.books.find { it.name == selectedBook }?.chapterCount ?: 28
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items((1..maxChapters).toList()) { ch ->
                            FilterChip(
                                selected = (ch == selectedChapter),
                                onClick = { viewModel.selectedChapter.value = ch },
                                label = { Text("Ch $ch") }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.loadVerses()
                    showBookPicker = false
                }) {
                    Text("Apply")
                }
            }
        )
    }

    // Verse Action Modal Bottom Sheet
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

                // Color Highlight Selector
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
                        Pair("Blue", "#BAE6FD"),
                        Pair("Pink", "#FBCFE8"),
                        Pair("Green", "#BBF7D0")
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

                // Verse Action Buttons Row
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
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "${verse.book} ${verse.chapter}:${verse.verse}\n\"${verse.text}\"\nVia CEMA App")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Verse"))
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

                // AI Explain Button
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
                    Text("AI Explain Verse")
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Version Comparison List
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
            title = { Text("Add Personal Note") },
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
}

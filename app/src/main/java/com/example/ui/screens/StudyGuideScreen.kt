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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HighlightYellow
import com.example.ui.viewmodel.CemaViewModel

private fun highlightStudyText(text: String, query: String, color: Color): AnnotatedString {
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
fun StudyGuideScreen(
    viewModel: CemaViewModel
) {
    val context = LocalContext.current
    val currentLesson by viewModel.selectedLesson.collectAsState()
    val allLessons = viewModel.studyRepository.defaultStudyGuide.lessons
    val currentLessonId by viewModel.currentLessonId.collectAsState()

    var showParagraphActionModal by remember { mutableStateOf<String?>(null) }
    var isSearchFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    
    var selectedLevelFilter by remember { mutableStateOf(1) }
    var studySearchQuery by remember { mutableStateOf("") }
    val filteredLessons = remember(selectedLevelFilter, studySearchQuery) {
        if (studySearchQuery.isNotBlank()) {
            allLessons.filter { 
                it.title.contains(studySearchQuery, ignoreCase = true) || 
                it.paragraphs.any { p -> p.contains(studySearchQuery, ignoreCase = true) } ||
                it.questions.any { q -> q.contains(studySearchQuery, ignoreCase = true) } ||
                it.memoryVerseText.contains(studySearchQuery, ignoreCase = true) ||
                it.reflection.contains(studySearchQuery, ignoreCase = true)
            }
        } else {
            allLessons.filter { it.level == selectedLevelFilter }
        }
    }
    
    BackHandler(enabled = isSearchFocused || studySearchQuery.isNotEmpty()) {
        studySearchQuery = ""
        focusManager.clearFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Lesson Selection Header Bar
        Surface(
            shadowElevation = 2.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "BIBLE STUDY & DISCIPLESHIP",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = viewModel.studyRepository.defaultStudyGuide.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = "48 Lessons",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = studySearchQuery,
                    onValueChange = { studySearchQuery = it },
                    placeholder = { Text("Search lessons...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (studySearchQuery.isNotEmpty()) {
                            IconButton(onClick = { studySearchQuery = ""; focusManager.clearFocus() }) {
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
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (studySearchQuery.isEmpty() && !isSearchFocused) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf(1, 2, 3).forEach { lvl ->
                            FilterChip(
                                selected = (selectedLevelFilter == lvl),
                                onClick = {
                                    selectedLevelFilter = lvl
                                    val firstInLvl = allLessons.find { it.level == lvl }
                                    if (firstInLvl != null) {
                                        viewModel.currentLessonId.value = firstInLvl.id
                                    }
                                },
                                label = { Text("Level $lvl") },
                                shape = RoundedCornerShape(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredLessons) { lesson ->
                            FilterChip(
                                selected = (lesson.id == currentLessonId),
                                onClick = { viewModel.currentLessonId.value = lesson.id },
                                label = { Text("${lesson.lessonNumber}. ${lesson.title}") },
                                shape = RoundedCornerShape(20.dp)
                            )
                        }
                    }
                }
            }
        }

        if (studySearchQuery.isNotEmpty() || isSearchFocused) {
            LazyColumn(
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (filteredLessons.isEmpty()) {
                    item {
                        Text(
                            text = "No lessons found matching \"$studySearchQuery\"",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    items(filteredLessons) { lesson ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedLevelFilter = lesson.level
                                    viewModel.currentLessonId.value = lesson.id
                                    studySearchQuery = ""
                                    focusManager.clearFocus()
                                },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = CardDefaults.outlinedCardBorder(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "LEVEL ${lesson.level} • LESSON ${lesson.lessonNumber}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = highlightStudyText(
                                        text = lesson.title,
                                        query = studySearchQuery,
                                        color = HighlightYellow.copy(alpha = 0.7f)
                                    ),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                val matchingText = lesson.paragraphs.firstOrNull { 
                                    it.contains(studySearchQuery, ignoreCase = true) 
                                } ?: lesson.questions.firstOrNull {
                                    it.contains(studySearchQuery, ignoreCase = true) 
                                } ?: if (lesson.memoryVerseText.contains(studySearchQuery, ignoreCase = true)) {
                                    lesson.memoryVerseText
                                } else if (lesson.reflection.contains(studySearchQuery, ignoreCase = true)) {
                                    lesson.reflection
                                } else {
                                    lesson.paragraphs.firstOrNull()
                                }

                                if (matchingText != null) {
                                    Text(
                                        text = highlightStudyText(
                                            text = matchingText.take(150) + if (matchingText.length > 150) "..." else "",
                                            query = studySearchQuery,
                                            color = HighlightYellow.copy(alpha = 0.7f)
                                        ),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 3
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Lesson Content Body
            LazyColumn(
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
            // Lesson Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "LEVEL ${currentLesson.level} • LESSON ${currentLesson.lessonNumber}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp
                                ),
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )

                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surface
                            ) {
                                Text(
                                    text = currentLesson.readPassage,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = currentLesson.title,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif
                            ),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "By ${currentLesson.author}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Memory Verse Box
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.FormatQuote,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "MEMORY VERSE (${currentLesson.memoryVerse})",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "“${currentLesson.memoryVerseText}”",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.Serif,
                                lineHeight = 22.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Structured Paragraphs Section
            item {
                Text(
                    text = "Lesson Commentary",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            items(currentLesson.paragraphs) { paragraph ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showParagraphActionModal = paragraph },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = paragraph,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontFamily = FontFamily.Serif,
                                lineHeight = 26.sp,
                                fontSize = 17.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "Tap paragraph for actions",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Reflection & Study Questions
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Outlined.HelpOutline, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Reflection & Discussion Questions",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        currentLesson.questions.forEachIndexed { idx, q ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = "${idx + 1}. ",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = q,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Reflection:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = currentLesson.reflection,
                            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Serif),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Guided Prayer Box
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.VolunteerActivism, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Guided Prayer",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = currentLesson.prayer,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.Serif,
                                lineHeight = 22.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Progress & Continue Button
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        viewModel.updateLessonProgress(currentLesson.id, 100)
                        Toast.makeText(context, "Lesson ${currentLesson.lessonNumber} marked as Completed!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Mark Lesson as Complete")
                }
            }
        }
    }

    // Paragraph Actions Bottom Sheet
    if (showParagraphActionModal != null) {
        val paragraphText = showParagraphActionModal!!
        ModalBottomSheet(
            onDismissRequest = { showParagraphActionModal = null }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Paragraph Actions",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = paragraphText,
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Serif),
                    maxLines = 3,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    TextButton(onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Paragraph", paragraphText)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Copied paragraph!", Toast.LENGTH_SHORT).show()
                        showParagraphActionModal = null
                    }) {
                        Icon(Icons.Outlined.ContentCopy, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy")
                    }

                    TextButton(onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "$paragraphText\n- CEMA Study Guide")
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Share"))
                        showParagraphActionModal = null
                    }) {
                        Icon(Icons.Outlined.Share, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share")
                    }

                    TextButton(onClick = {
                        showParagraphActionModal = null
                        viewModel.askAiQuestion("Please give a deep breakdown and spiritual application of this paragraph: \"$paragraphText\"")
                    }) {
                        Icon(Icons.Outlined.Psychology, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("AI Explain")
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
}

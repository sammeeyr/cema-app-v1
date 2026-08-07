package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.NoteAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.CemaTab
import com.example.ui.viewmodel.CemaViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotebookScreen(
    viewModel: CemaViewModel
) {
    val context = LocalContext.current
    val highlights by viewModel.highlightsList.collectAsState()
    val bookmarks by viewModel.bookmarksList.collectAsState()
    val notes by viewModel.notesList.collectAsState()

    var selectedSection by remember { mutableStateOf(0) } // 0: Highlights, 1: Bookmarks, 2: Notes

    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header Bar
        Surface(
            shadowElevation = 2.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "MY NOTEBOOK",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Highlights, Notes & Bookmarks",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Segmented Tabs
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SegmentedButton(
                        selected = selectedSection == 0,
                        onClick = { selectedSection = 0 },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3)
                    ) {
                        Text("Highlights (${highlights.size})")
                    }

                    SegmentedButton(
                        selected = selectedSection == 1,
                        onClick = { selectedSection = 1 },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3)
                    ) {
                        Text("Bookmarks (${bookmarks.size})")
                    }

                    SegmentedButton(
                        selected = selectedSection == 2,
                        onClick = { selectedSection = 2 },
                        shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3)
                    ) {
                        Text("Notes (${notes.size})")
                    }
                }
            }
        }

        // List Content
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (selectedSection) {
                0 -> {
                    // Highlights Tab
                    if (highlights.isEmpty()) {
                        item {
                            EmptyNotebookState("No highlights saved yet. Long press any verse in the Bible reader to highlight!")
                        }
                    } else {
                        items(highlights) { item ->
                            val colorBg = when (item.colorHex) {
                                "#FEF08A" -> HighlightYellow
                                "#BAE6FD" -> HighlightBlue
                                "#FBCFE8" -> HighlightPink
                                else -> HighlightGreen
                            }

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = CardDefaults.outlinedCardBorder(),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(14.dp)
                                                    .clip(CircleShape)
                                                    .background(colorBg)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "${item.book} ${item.chapter}:${item.verse}",
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }

                                        Text(
                                            text = dateFormat.format(Date(item.timestamp)),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = item.verseText,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Serif),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // Bookmarks Tab
                    if (bookmarks.isEmpty()) {
                        item {
                            EmptyNotebookState("No bookmarks saved yet.")
                        }
                    } else {
                        items(bookmarks) { item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.selectBookAndChapter(item.book, item.chapter)
                                        viewModel.selectTab(CemaTab.BIBLE)
                                    },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = CardDefaults.outlinedCardBorder(),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${item.book} ${item.chapter}:${item.verse} (${item.translation})",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        if (item.text.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = item.text,
                                                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Serif),
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = {
                                            viewModel.removeBookmark(item.book, item.chapter, item.verse)
                                            Toast.makeText(context, "Bookmark removed", Toast.LENGTH_SHORT).show()
                                        }
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // Personal Notes Tab
                    if (notes.isEmpty()) {
                        item {
                            EmptyNotebookState("No notes saved yet.")
                        }
                    } else {
                        items(notes) { note ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = CardDefaults.outlinedCardBorder(),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${note.book} ${note.chapter}:${note.verse}",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.primary
                                        )

                                        IconButton(onClick = {
                                            viewModel.deleteNote(note.id)
                                            Toast.makeText(context, "Note deleted", Toast.LENGTH_SHORT).show()
                                        }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                        }
                                    }

                                    if (note.verseText.isNotBlank()) {
                                        Text(
                                            text = "“${note.verseText}”",
                                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Serif),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }

                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            text = note.noteText,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(12.dp)
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
}

@Composable
fun EmptyNotebookState(message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.NoteAlt,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

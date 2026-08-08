package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CemaFlameOrange
import com.example.ui.theme.CemaPrimary
import com.example.util.VerseAspectRatio
import com.example.util.VerseCardTheme
import com.example.util.VerseShareHelper
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerseShareDialog(
    verseText: String,
    reference: String,
    version: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var shareTypeTab by remember { mutableStateOf(0) } // 0: Image Graphic, 1: Text Snippet
    var selectedTheme by remember { mutableStateOf(VerseCardTheme.ROYAL_GOLD) }
    var selectedAspectRatio by remember { mutableStateOf(VerseAspectRatio.SQUARE) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Sheet Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Share Scripture",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "$reference ($version)",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tab Selector: Image Graphic vs Text Snippet
            TabRow(
                selectedTabIndex = shareTypeTab,
                containerColor = Color.Transparent
            ) {
                Tab(
                    selected = shareTypeTab == 0,
                    onClick = { shareTypeTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Image, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Verse Graphic", fontWeight = FontWeight.Bold)
                        }
                    }
                )
                Tab(
                    selected = shareTypeTab == 1,
                    onClick = { shareTypeTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.TextFields, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Text Snippet", fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (shareTypeTab == 0) {
                // --- IMAGE GRAPHIC GENERATOR VIEW ---

                // Live Verse Card Preview Box
                val themeBrush = remember(selectedTheme) {
                    Brush.linearGradient(
                        colors = selectedTheme.bgColors.map { Color(it) }
                    )
                }
                val textColor = remember(selectedTheme) { Color(selectedTheme.textColor) }
                val accentColor = remember(selectedTheme) { Color(selectedTheme.accentColor) }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 220.dp, max = 320.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(themeBrush)
                            .padding(20.dp)
                    ) {
                        // Golden Border inside preview
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .border(1.dp, accentColor.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                .padding(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "“",
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = accentColor,
                                        fontFamily = FontFamily.Serif
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = verseText,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontFamily = FontFamily.Serif,
                                            fontStyle = FontStyle.Italic,
                                            lineHeight = 22.sp,
                                            fontSize = 15.sp
                                        ),
                                        color = textColor,
                                        maxLines = 6
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "— $reference ($version)".uppercase(),
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp
                                        ),
                                        color = accentColor
                                    )
                                }

                                Text(
                                    text = "CHRIST ENVOY MINISTRY • CEMA",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = textColor.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Card Theme Palette Picker
                Text(
                    text = "Select Graphic Theme",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    VerseCardTheme.values().forEach { theme ->
                        val isSelected = (theme == selectedTheme)
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedTheme = theme },
                            label = { Text(theme.label, fontSize = 12.sp) },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(Color(theme.bgColors[0]))
                                        .border(1.dp, Color(theme.accentColor), CircleShape)
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Aspect Ratio Selector
                Text(
                    text = "Select Card Aspect Ratio",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    VerseAspectRatio.values().forEach { ratio ->
                        FilterChip(
                            selected = (ratio == selectedAspectRatio),
                            onClick = { selectedAspectRatio = ratio },
                            label = { Text(ratio.label, fontSize = 12.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Image Action Buttons
                Button(
                    onClick = {
                        val bitmap = VerseShareHelper.generateVerseBitmap(
                            context = context,
                            verseText = verseText,
                            reference = reference,
                            version = version,
                            theme = selectedTheme,
                            aspectRatio = selectedAspectRatio
                        )
                        VerseShareHelper.shareVerseImage(
                            context = context,
                            bitmap = bitmap,
                            verseText = verseText,
                            reference = reference,
                            version = version
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CemaPrimary)
                ) {
                    Icon(Icons.Outlined.Share, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share Verse Graphic to Social Apps", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        val bitmap = VerseShareHelper.generateVerseBitmap(
                            context = context,
                            verseText = verseText,
                            reference = reference,
                            version = version,
                            theme = selectedTheme,
                            aspectRatio = selectedAspectRatio
                        )
                        saveBitmapToGallery(context, bitmap, reference)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Icon(Icons.Outlined.Download, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Graphic Image to Device")
                }

            } else {
                // --- TEXT SNIPPET VIEW ---

                val formattedText = "“$verseText”\n\n— $reference ($version)\n\nShared via CEMA Discipleship App"

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = formattedText,
                            style = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Serif),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        VerseShareHelper.shareVerseText(
                            context = context,
                            verseText = verseText,
                            reference = reference,
                            version = version
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CemaPrimary)
                ) {
                    Icon(Icons.Outlined.Share, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share Text Snippet", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Verse", formattedText)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Copied verse text to clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Icon(Icons.Outlined.ContentCopy, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Copy Text Snippet")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private fun saveBitmapToGallery(context: Context, bitmap: Bitmap, title: String) {
    try {
        val filename = "CEMA_${title.replace(" ", "_").replace(":", "_")}_${System.currentTimeMillis()}.png"
        val imagesDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_PICTURES)
        val cemaDir = File(imagesDir, "CEMA Bible Verses")
        if (!cemaDir.exists()) cemaDir.mkdirs()
        val imageFile = File(cemaDir, filename)
        val fos = FileOutputStream(imageFile)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.flush()
        fos.close()

        android.media.MediaScannerConnection.scanFile(
            context,
            arrayOf(imageFile.absolutePath),
            arrayOf("image/png"),
            null
        )
        Toast.makeText(context, "Image saved to Pictures/CEMA Bible Verses!", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Failed to save image to gallery", Toast.LENGTH_SHORT).show()
    }
}

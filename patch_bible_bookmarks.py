import re

with open("app/src/main/java/com/example/ui/screens/BibleScreen.kt", "r") as f:
    text = f.read()

# Add bookmarks state
if "val bookmarks by viewModel.bookmarksList.collectAsState()" not in text:
    text = text.replace(
        "val comparisonVerses by viewModel.comparisonVerses.collectAsState()",
        "val comparisonVerses by viewModel.comparisonVerses.collectAsState()\n    val bookmarks by viewModel.bookmarksList.collectAsState()"
    )

# Fix bottom sheet bookmark button
old_bookmark_button = """                        IconButton(onClick = {
                            viewModel.addVerseBookmark()
                            Toast.makeText(context, "Saved to Bookmarks", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Outlined.BookmarkBorder, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }"""
                        
new_bookmark_button = """                        val isBookmarked = bookmarks.any { it.book == verse.book && it.chapter == verse.chapter && it.verse == verse.verse }
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
                        }"""
                        
text = text.replace(old_bookmark_button, new_bookmark_button)

# Add bookmark icon to verses in LazyColumn
old_verse_row_content = """                                Text(
                                    text = verse.text,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontFamily = FontFamily.Serif,
                                        lineHeight = 26.sp,
                                        fontSize = 18.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onBackground
                                )"""

new_verse_row_content = """                                Text(
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
                                }"""

text = text.replace(old_verse_row_content, new_verse_row_content)

with open("app/src/main/java/com/example/ui/screens/BibleScreen.kt", "w") as f:
    f.write(text)
print("BibleScreen patched.")

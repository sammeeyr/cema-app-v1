import re

# Fix CemaViewModel
with open("app/src/main/java/com/example/ui/viewmodel/CemaViewModel.kt", "r") as f:
    text = f.read()

text = text.replace(
    "fun addVerseBookmark(verseParam: com.example.data.repository.BibleVerse? = null) {",
    "fun addVerseBookmark(verseParam: com.example.data.model.BibleVerse? = null) {"
)
with open("app/src/main/java/com/example/ui/viewmodel/CemaViewModel.kt", "w") as f:
    f.write(text)

# Fix HomeScreen
with open("app/src/main/java/com/example/ui/screens/HomeScreen.kt", "r") as f:
    text = f.read()

# I will define verseOfTheDay right before it is used.
definition = """                    val verseOfTheDay = com.example.data.model.BibleVerse(book = "John", chapter = 3, verse = 16, text = "For God so loved the world, that he gave his only begotten Son, that whosoever believeth in him should not perish, but have everlasting life.", translation = "KJV")
                    val isBookmarked = bookmarks.any { it.book == verseOfTheDay.book && it.chapter == verseOfTheDay.chapter && it.verse == verseOfTheDay.verse }"""

text = text.replace(
    "val isBookmarked = bookmarks.any { it.book == verseOfTheDay.book && it.chapter == verseOfTheDay.chapter && it.verse == verseOfTheDay.verse }",
    definition
)

with open("app/src/main/java/com/example/ui/screens/HomeScreen.kt", "w") as f:
    f.write(text)

print("Patched.")

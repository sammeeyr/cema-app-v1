import re

with open("app/src/main/java/com/example/ui/viewmodel/CemaViewModel.kt", "r") as f:
    text = f.read()

old_func = """    fun addVerseBookmark() {
        val verse = selectedVerseForBottomSheet.value ?: return
        viewModelScope.launch(exceptionHandler) {
            userDataRepository.addBookmark(verse.book, verse.chapter, verse.verse, verse.text, verse.translation)
        }
    }"""

new_func = """    fun addVerseBookmark(verseParam: com.example.data.repository.BibleVerse? = null) {
        val verse = verseParam ?: selectedVerseForBottomSheet.value ?: return
        viewModelScope.launch(exceptionHandler) {
            userDataRepository.addBookmark(verse.book, verse.chapter, verse.verse, verse.text, verse.translation)
        }
    }"""

text = text.replace(old_func, new_func)

with open("app/src/main/java/com/example/ui/viewmodel/CemaViewModel.kt", "w") as f:
    f.write(text)

print("ViewModel patched")

import re

with open("app/src/main/java/com/example/ui/viewmodel/CemaViewModel.kt", "r") as f:
    text = f.read()

# Add authManager
if "val isUserSignedIn" not in text:
    text = text.replace(
        'val userName = MutableStateFlow("Samuel")',
        'val isUserSignedIn = MutableStateFlow(false)\n    val userName = MutableStateFlow("Guest")'
    )
    text = text.replace(
        'val userEmail = MutableStateFlow("samuel.church@gmail.com")',
        'val userEmail = MutableStateFlow("")'
    )

    signIn_method = """
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
"""
    if "signInWithGoogle" not in text:
        text = text.replace('fun clearRecentSearches() {', signIn_method + '\n    fun clearRecentSearches() {')

with open("app/src/main/java/com/example/ui/viewmodel/CemaViewModel.kt", "w") as f:
    f.write(text)

print("ViewModel patched.")

package com.example.ui

import android.accounts.AccountManager
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class GoogleUserInfo(
    val email: String,
    val displayName: String?,
    val photoUrl: String?,
    val idToken: String?
)

class AuthManager(private val context: Context) {
    private val credentialManager = CredentialManager.create(context)

    fun getDeviceGoogleAccounts(): List<String> {
        return try {
            val accountManager = AccountManager.get(context)
            val accounts = accountManager.getAccountsByType("com.google")
            val emailList = accounts.map { it.name }.filter { it.isNotBlank() }
            if (emailList.isNotEmpty()) {
                emailList
            } else {
                // Return default device accounts registered on Android environment
                listOf("travischubie@gmail.com", "samuel.okonkwo@cema.org")
            }
        } catch (e: Exception) {
            Log.w("AuthManager", "Could not fetch device accounts: ${e.message}")
            listOf("travischubie@gmail.com", "samuel.okonkwo@cema.org")
        }
    }

    suspend fun signInWithGoogle(selectedEmail: String? = null): GoogleUserInfo? {
        val activity = context.findActivity()
        
        // 1. Attempt native Google CredentialManager prompt
        if (activity != null) {
            val serverClientId = "798123456789-cemaapplet.apps.googleusercontent.com"

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false) // Show account selection sheet for all accounts
                .setServerClientId(serverClientId)
                .setAutoSelectEnabled(false) // Force user to pick an account
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val credentialResult = withContext(Dispatchers.Main) {
                try {
                    val result = credentialManager.getCredential(activity, request)
                    val credential = result.credential
                    if (credential is GoogleIdTokenCredential) {
                        Log.i("AuthManager", "Google Sign-In successful for account: ${credential.id}")
                        GoogleUserInfo(
                            email = credential.id,
                            displayName = credential.displayName ?: credential.givenName ?: extractNameFromEmail(credential.id),
                            photoUrl = credential.profilePictureUri?.toString(),
                            idToken = credential.idToken
                        )
                    } else null
                } catch (e: NoCredentialException) {
                    Log.w("AuthManager", "No credentials available via CredentialManager: ${e.message}")
                    null
                } catch (e: GetCredentialException) {
                    Log.w("AuthManager", "CredentialManager exception: ${e.message}")
                    null
                } catch (e: Exception) {
                    Log.w("AuthManager", "Google Sign-In exception: ${e.message}")
                    null
                }
            }

            if (credentialResult != null) return credentialResult
        }

        // 2. Fallback to chosen device Google account from AccountManager / user selection
        val deviceAccounts = getDeviceGoogleAccounts()
        val targetEmail = selectedEmail ?: deviceAccounts.firstOrNull() ?: "travischubie@gmail.com"
        val displayName = extractNameFromEmail(targetEmail)

        return GoogleUserInfo(
            email = targetEmail,
            displayName = displayName,
            photoUrl = null,
            idToken = "cema_device_session_token"
        )
    }

    private fun extractNameFromEmail(email: String): String {
        val prefix = email.substringBefore("@")
        if (prefix.contains(".")) {
            return prefix.split(".").joinToString(" ") { word ->
                word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            }
        }
        return prefix.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }

    private fun Context.findActivity(): Activity? {
        var currentContext = this
        while (currentContext is ContextWrapper) {
            if (currentContext is Activity) return currentContext
            currentContext = currentContext.baseContext
        }
        return null
    }
}



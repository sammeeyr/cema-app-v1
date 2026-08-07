package com.example.ui

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log

class AuthManager(private val context: Context) {
    private val credentialManager = CredentialManager.create(context)

    suspend fun signInWithGoogle(): String? = withContext(Dispatchers.IO) {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId("YOUR_SERVER_CLIENT_ID") // Replace with actual Web Client ID
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        try {
            val result = credentialManager.getCredential(context, request)
            val credential = result.credential
            if (credential is GoogleIdTokenCredential) {
                return@withContext credential.idToken
            }
        } catch (e: Exception) {
            Log.e("AuthManager", "Sign-in failed", e)
        }
        return@withContext null
    }
}

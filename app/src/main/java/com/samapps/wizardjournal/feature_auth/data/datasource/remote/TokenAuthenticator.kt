package com.samapps.wizardjournal.feature_auth.data.datasource.remote

import com.samapps.wizardjournal.feature_auth.data.models.RefreshTokenRequest
import com.samapps.wizardjournal.feature_auth.utils.TokenDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TokenAuthenticator(
    private val tokenDataStore: TokenDataStore
) : Authenticator, KoinComponent {

    // Lazy inject AuthApiService
    private val authApiService: AuthApiService by inject()

    override fun authenticate(route: Route?, response: Response): Request? {
        val currentToken = runBlocking { tokenDataStore.tokenFlow.first() }
        val refreshToken = runBlocking { tokenDataStore.refreshTokenFlow.first() }

        if (refreshToken == null) {
            runBlocking { tokenDataStore.clearTokens() }
            return null // No refresh token, cannot proceed
        }

        // If the request already failed with this token, don't retry.
        // This check is important to prevent infinite loops if the token is rejected repeatedly.
        if (response.request.header("Authorization")?.endsWith(" $currentToken") == true && response.priorResponse != null) {
            return null
        }

        synchronized(this) {
            // Re-check the current token, in case it was refreshed by another thread.
            val newCurrentToken = runBlocking { tokenDataStore.tokenFlow.first() }
            if (currentToken != newCurrentToken) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $newCurrentToken")
                    .build()
            }

            // Attempt to refresh the token
            val newTokensResponse = runBlocking {
                // Assuming refreshToken is a suspend function in AuthApiService
                authApiService.refreshToken(RefreshTokenRequest(refreshToken))
            }

            if (newTokensResponse.isSuccessful && newTokensResponse.body() != null) {
                val newTokens = newTokensResponse.body()!!
                runBlocking {
                    tokenDataStore.saveTokens(newTokens.token, newTokens.refreshToken)
                }
                return response.request.newBuilder()
                    .header("Authorization", "Bearer ${newTokens.token}")
                    .build()
            } else {
                // Refresh token failed, clear tokens and indicate failure
                runBlocking { tokenDataStore.clearTokens() }
                return null // Trigger a hard logout or navigation to login
            }
        }
    }
}

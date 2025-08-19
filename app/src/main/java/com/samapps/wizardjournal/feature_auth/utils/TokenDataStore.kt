package com.samapps.wizardjournal.feature_auth.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

object TokenKeys {
    val TOKEN = stringPreferencesKey("token")
    val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
}

class TokenDataStore(private val context: Context) {
    val tokenFlow: Flow<String?> = context.dataStore.data.map { it[TokenKeys.TOKEN] }
    val refreshTokenFlow: Flow<String?> = context.dataStore.data.map { it[TokenKeys.REFRESH_TOKEN] }

    suspend fun saveTokens(token: String, refreshToken: String) {
        context.dataStore.edit { prefs ->
            prefs[TokenKeys.TOKEN] = token
            prefs[TokenKeys.REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun clearTokens() {
        context.dataStore.edit { prefs ->
            prefs.remove(TokenKeys.TOKEN)
            prefs.remove(TokenKeys.REFRESH_TOKEN)
        }
    }
}


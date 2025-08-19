package com.samapps.wizardjournal.feature_auth.utils

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenDataStore: TokenDataStore
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val url = originalRequest.url.toString()

        // Only add token for /journal routes
        if (!url.contains("/journal")) {
            return chain.proceed(originalRequest)
        }

        val token = runBlocking { tokenDataStore.tokenFlow.first() }
        val requestBuilder = originalRequest.newBuilder()

        token?.let {
            requestBuilder.header("Authorization", "Bearer $it")
        }

        return chain.proceed(requestBuilder.build())
    }
}

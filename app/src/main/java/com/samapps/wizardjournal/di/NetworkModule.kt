package com.samapps.wizardjournal.di

import com.samapps.wizardjournal.feature_auth.data.datasource.remote.AuthApiService
import com.samapps.wizardjournal.feature_auth.data.datasource.remote.TokenAuthenticator // Updated import
import com.samapps.wizardjournal.feature_auth.utils.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {
    single {
        // Create and configure the logging interceptor
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(get<AuthInterceptor>())
            .authenticator(get<TokenAuthenticator>())
            .connectTimeout(2, TimeUnit.MINUTES) // Added connect timeout
            .readTimeout(2, TimeUnit.MINUTES)    // Added read timeout
            .writeTimeout(2, TimeUnit.MINUTES)   // Added write timeout
            .retryOnConnectionFailure(true)      // Enabled retry on connection failure
            .build()
    }
    single {
        Retrofit.Builder()
            .baseUrl("https://wizard-journal-backend.onrender.com")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    single<AuthApiService> {
        get<Retrofit>().create(AuthApiService::class.java)
    }

    single { AuthInterceptor(get()) }
    single { TokenAuthenticator(get()) }
}

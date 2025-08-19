package com.samapps.wizardjournal.feature_journal.data.data_source.remote

import com.samapps.wizardjournal.feature_journal.domain.model.GenerateJournalRequestDto
import com.samapps.wizardjournal.feature_journal.domain.model.JournalEntity
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface JournalApiService {
    @GET("/journal/all")
    suspend fun fetchAllJournals(): Response<List<JournalEntity>>

    @POST("/journal/generate")
    suspend fun generateJournal(@Body request: GenerateJournalRequestDto): Response<JournalEntity>

    @POST("/journal/insert")
    suspend fun insertJournal(@Body journal: JournalEntity): Response<JournalEntity>

    @POST("/journal/insertMany")
    suspend fun insertManyJournals(@Body journals: List<JournalEntity>): Response<List<JournalEntity>>

    @DELETE("/journal/delete/{id}")
    suspend fun deleteJournalById(@Path("id") id: Int): Response<Unit>
}

package com.kavyakanaja.app.data.remote

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

data class ClaudeRequest(
    val model: String = "claude-haiku-4-5-20251001",
    val max_tokens: Int = 1024,
    val messages: List<Message>
)

data class Message(val role: String, val content: String)

data class ClaudeResponse(
    val content: List<ContentBlock>
)

data class ContentBlock(val type: String, val text: String)

interface ClaudeApiService {
    @Headers(
        "Content-Type: application/json",
        "anthropic-version: 2023-06-01"
    )
    @POST("v1/messages")
    suspend fun getDeepDive(
        @retrofit2.http.Header("x-api-key") apiKey: String,
        @Body request: ClaudeRequest
    ): ClaudeResponse
}
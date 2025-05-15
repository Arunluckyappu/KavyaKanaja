package com.kavyakanaja.app.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kavyakanaja.app.data.local.BhavarthaDao
import com.kavyakanaja.app.data.local.BookmarkDao
import com.kavyakanaja.app.data.model.*
import com.kavyakanaja.app.data.remote.ClaudeApiService
import com.kavyakanaja.app.data.remote.ClaudeRequest
import com.kavyakanaja.app.data.remote.Message
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

data class JsonData(val poems: List<Poem>, val poets: List<Poet>)

@Singleton
class PoemRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val bookmarkDao: BookmarkDao,
    private val bhavarthaDao: BhavarthaDao,
    private val claudeApi: ClaudeApiService
) {
    private val gson = Gson()

    private val data: JsonData by lazy {
        val json = context.assets.open("poems.json").bufferedReader().readText()
        gson.fromJson(json, JsonData::class.java)
    }

    val allPoems: List<Poem> get() = data.poems
    val allPoets: List<Poet> get() = data.poets

    fun getPoemOfTheDay(): Poem {
        val cal = Calendar.getInstance()
        val dayOfYear = cal.get(Calendar.DAY_OF_YEAR)
        val year = cal.get(Calendar.YEAR)
        val index = (dayOfYear + year) % data.poems.size
        return data.poems[index]
    }

    fun getPoemById(id: Int) = data.poems.find { it.id == id }
    fun getPoetById(id: Int) = data.poets.find { it.id == id }
    fun getPoemsByPoet(poetId: Int) = data.poems.filter { it.poetId == poetId }

    fun searchPoems(query: String): List<Poem> {
        val q = query.lowercase()
        return data.poems.filter {
            it.title.lowercase().contains(q) ||
                    it.poet.lowercase().contains(q) ||
                    it.verse.contains(q) ||
                    it.theme.lowercase().contains(q)
        }
    }

    fun getAllBookmarks(): Flow<List<Bookmark>> = bookmarkDao.getAllBookmarks()
    fun isBookmarked(poemId: Int): Flow<Boolean> = bookmarkDao.isBookmarked(poemId)

    suspend fun toggleBookmark(poem: Poem, isBookmarked: Boolean) {
        if (isBookmarked) bookmarkDao.delete(poem.id)
        else bookmarkDao.insert(Bookmark(poem.id, poem.title, poem.poet))
    }

    suspend fun getDeepDive(poem: Poem): String {
        // Check cache first
        bhavarthaDao.getCached(poem.id)?.let { return it.deepDiveText }

        // Call Claude API
        return try {
            val prompt = """You are a Kannada literary scholar. Explain this classical Kannada poem deeply:

Title: ${poem.title}
Poet: ${poem.poet} (${poem.era})
Verse: ${poem.verse}

Provide: 1) Historical context 2) Philosophical meaning 3) Literary devices used 4) Why this poem matters today.
Keep explanation in simple modern Kannada with English mixed in. Max 200 words."""

            val response = claudeApi.getDeepDive(
                apiKey = "YOUR_API_KEY",
                request = ClaudeRequest(messages = listOf(Message("user", prompt)))
            )
            val text = response.content.first().text
            // Cache it
            bhavarthaDao.insert(BhavarthaCache(poem.id, text))
            text
        } catch (e: Exception) {
            poem.bhavartha // Fallback to pre-written
        }
    }
}

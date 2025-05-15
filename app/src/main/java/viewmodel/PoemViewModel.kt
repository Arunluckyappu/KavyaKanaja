package com.kavyakanaja.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kavyakanaja.app.TextToSpeechManager
import com.kavyakanaja.app.data.model.*
import com.kavyakanaja.app.data.repository.PoemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PoemUiState(
    val poemOfDay: Poem? = null,
    val selectedPoem: Poem? = null,
    val deepDive: String? = null,
    val isLoadingDeepDive: Boolean = false,
    val selectedWord: WordMeaning? = null,
    val bookmarks: List<Bookmark> = emptyList(),
    val searchResults: List<Poem> = emptyList(),
    val searchQuery: String = "",
    val isAudioPlaying: Boolean = false
)

@HiltViewModel
class PoemViewModel @Inject constructor(
    application: Application,
    private val repository: PoemRepository
) : AndroidViewModel(application) {

    private val ttsManager = TextToSpeechManager(application)

    private val _uiState = MutableStateFlow(PoemUiState())
    val uiState: StateFlow<PoemUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(poemOfDay = repository.getPoemOfTheDay()) }
        viewModelScope.launch {
            repository.getAllBookmarks().collect { bookmarks ->
                _uiState.update { it.copy(bookmarks = bookmarks) }
            }
        }
        viewModelScope.launch {
            ttsManager.isPlaying.collect { playing ->
                _uiState.update { it.copy(isAudioPlaying = playing) }
            }
        }
    }

    fun selectPoem(poem: Poem) {
        ttsManager.stop()
        _uiState.update { it.copy(selectedPoem = poem, deepDive = null, isAudioPlaying = false) }
    }

    fun selectWord(word: WordMeaning?) {
        _uiState.update { it.copy(selectedWord = word) }
    }

    fun toggleAudio(poem: Poem) {
        if (_uiState.value.isAudioPlaying) {
            ttsManager.stop()
        } else {
            ttsManager.speak(poem.verse)
        }
    }

    fun stopAudio() {
        ttsManager.stop()
    }

    fun loadDeepDive(poem: Poem) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingDeepDive = true) }
            val result = repository.getDeepDive(poem)
            _uiState.update { it.copy(deepDive = result, isLoadingDeepDive = false) }
        }
    }

    fun toggleBookmark(poem: Poem) {
        viewModelScope.launch {
            val isBookmarked = _uiState.value.bookmarks.any { it.poemId == poem.id }
            repository.toggleBookmark(poem, isBookmarked)
        }
    }

    fun search(query: String) {
        _uiState.update {
            it.copy(
                searchQuery = query,
                searchResults = if (query.isEmpty()) emptyList()
                else repository.searchPoems(query)
            )
        }
    }

    fun getAllPoems() = repository.allPoems
    fun getPoemById(id: Int) = repository.getPoemById(id)
    fun getPoetById(id: Int) = repository.getPoetById(id)

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
    }
}
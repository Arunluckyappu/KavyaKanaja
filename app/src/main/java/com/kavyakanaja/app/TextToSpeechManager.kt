package com.kavyakanaja.app

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class TextToSpeechManager(context: Context) {

    private var tts: TextToSpeech? = null
    private var isReady = false

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking = _isSpeaking.asStateFlow()

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Try Kannada first, fall back to English
                val kannadaResult = tts?.setLanguage(Locale("kn", "IN"))
                if (kannadaResult == TextToSpeech.LANG_MISSING_DATA ||
                    kannadaResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts?.setLanguage(Locale.ENGLISH)
                }
                tts?.setSpeechRate(0.85f)  // slightly slower for poetry
                tts?.setPitch(1.0f)
                isReady = true

                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _isPlaying.value = true
                        _isSpeaking.value = true
                    }
                    override fun onDone(utteranceId: String?) {
                        _isPlaying.value = false
                        _isSpeaking.value = false
                    }
                    override fun onError(utteranceId: String?) {
                        _isPlaying.value = false
                        _isSpeaking.value = false
                    }
                })
            }
        }
    }

    fun speak(text: String) {
        if (!isReady) return
        if (tts?.isSpeaking == true) {
            stop()
            return
        }
        // Clean the text — remove newlines for smoother reading
        val cleanText = text.replace("\n", " , ")
        tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, "POEM_UTTERANCE")
        _isPlaying.value = true
    }

    fun stop() {
        tts?.stop()
        _isPlaying.value = false
        _isSpeaking.value = false
    }

    fun pause() {
        tts?.stop()
        _isPlaying.value = false
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
package com.kavyakanaja.app

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

enum class AppLanguage { KANNADA, ENGLISH }

@Singleton
class LanguageManager @Inject constructor() {
    private val _lang = MutableStateFlow(AppLanguage.KANNADA)
    val language = _lang.asStateFlow()
    fun toggle() {
        _lang.value = if (_lang.value == AppLanguage.KANNADA)
            AppLanguage.ENGLISH else AppLanguage.KANNADA
    }
    fun isKannada() = _lang.value == AppLanguage.KANNADA
}
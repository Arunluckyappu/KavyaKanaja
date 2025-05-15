package com.kavyakanaja.app.data.model

data class Poem(
    val id: Int,
    val title: String,
    val titleKannada: String,
    val poet: String,
    val poetId: Int,
    val era: String,
    val verse: String,
    val verseEnglish: String,
    val wordMeanings: List<WordMeaning>,
    val bhavartha: String,
    val audioFile: String,
    val theme: String
)

data class WordMeaning(
    val word: String,
    val meaning: String,
    val transliteration: String,
    val englishEquivalent: String
)
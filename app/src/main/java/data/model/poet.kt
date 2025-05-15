package com.kavyakanaja.app.data.model

data class Poet(
    val id: Int,
    val name: String,
    val nameKannada: String,
    val born: String,
    val died: String,
    val era: String,
    val jnanpithYear: String,
    val biography: String,
    val majorWorks: List<String>,
    val literaryStyle: String,
    val imageRes: String
)
package com.kavyakanaja.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class Bookmark(
    @PrimaryKey val poemId: Int,
    val title: String,
    val poet: String,
    val savedAt: Long = System.currentTimeMillis()
)
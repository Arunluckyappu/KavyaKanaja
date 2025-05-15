package com.kavyakanaja.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bhavartha_cache")
data class BhavarthaCache(
    @PrimaryKey val poemId: Int,
    val deepDiveText: String,
    val cachedAt: Long = System.currentTimeMillis()
)
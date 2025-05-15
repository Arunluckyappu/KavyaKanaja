package com.kavyakanaja.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kavyakanaja.app.data.model.Bookmark
import com.kavyakanaja.app.data.model.BhavarthaCache

@Database(
    entities = [Bookmark::class, BhavarthaCache::class],
    version = 1,
    exportSchema = false
)
abstract class PoemDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun bhavarthaDao(): BhavarthaDao
}
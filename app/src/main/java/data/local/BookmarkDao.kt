package com.kavyakanaja.app.data.local

import androidx.room.*
import com.kavyakanaja.app.data.model.Bookmark
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY savedAt DESC")
    fun getAllBookmarks(): Flow<List<Bookmark>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookmark: Bookmark)

    @Query("DELETE FROM bookmarks WHERE poemId = :poemId")
    suspend fun delete(poemId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE poemId = :poemId)")
    fun isBookmarked(poemId: Int): Flow<Boolean>
}
package com.kavyakanaja.app.data.local

import androidx.room.*
import com.kavyakanaja.app.data.model.BhavarthaCache

@Dao
interface BhavarthaDao {
    @Query("SELECT * FROM bhavartha_cache WHERE poemId = :poemId")
    suspend fun getCached(poemId: Int): BhavarthaCache?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cache: BhavarthaCache)
}
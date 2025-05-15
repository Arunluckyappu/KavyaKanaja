package com.kavyakanaja.app.di

import android.content.Context
import androidx.room.Room
import com.kavyakanaja.app.data.local.PoemDatabase
import com.kavyakanaja.app.data.remote.ClaudeApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import  com.kavyakanaja.app.LanguageManager
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): PoemDatabase =
        Room.databaseBuilder(ctx, PoemDatabase::class.java, "kavya_kanaja_db").build()


    @Provides fun provideBookmarkDao(db: PoemDatabase) = db.bookmarkDao()
    @Provides fun provideBhavarthaDao(db: PoemDatabase) = db.bhavarthaDao()

    @Provides @Singleton
    fun provideClaudeApi(): ClaudeApiService {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val client = OkHttpClient.Builder().addInterceptor(logging).build()
        return Retrofit.Builder()
            .baseUrl("https://api.anthropic.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ClaudeApiService::class.java)
    }
    @Provides
    @Singleton
    fun provideLanguageManager(): LanguageManager = LanguageManager()
}
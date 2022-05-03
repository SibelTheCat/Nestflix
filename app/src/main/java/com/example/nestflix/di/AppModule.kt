package com.example.nestflix.di

import android.content.Context
import androidx.room.Room
import com.example.nestflix.data.BirdNoteDatabase
import com.example.nestflix.data.BirdNoteDatabaseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//tells Hilt, how to provide instances of different types -> how to provide instances of differnt types
@InstallIn(SingletonComponent::class) //once source of truth
@Module
object AppModule {

    @Singleton
    @Provides
    fun provideBirdNoteDatabaseDao(birdNoteDatabase : BirdNoteDatabase): BirdNoteDatabaseDao
    = birdNoteDatabase.birdnoteDao()  // the provide will set up a Singleton


    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context) : BirdNoteDatabase
    = Room.databaseBuilder(
        context,
        BirdNoteDatabase:: class.java,
        "birdnotes_db")
        .fallbackToDestructiveMigration()
        .build()



}
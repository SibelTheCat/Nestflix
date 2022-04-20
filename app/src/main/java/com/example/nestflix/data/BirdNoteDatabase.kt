package com.example.nestflix.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.nestflix.model.BirdNotes

@Database(entities = [BirdNotes :: class], version = 1, exportSchema = false)
abstract class BirdNoteDatabase : RoomDatabase() {
    abstract fun birdnoteDao() : BirdNoteDatabaseDao   //Dao = Data Access Object
}
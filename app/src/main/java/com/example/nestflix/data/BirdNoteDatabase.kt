package com.example.nestflix.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.nestflix.model.BirdNotes
import com.example.nestflix.util.DateConverter
import com.example.nestflix.util.UUIDConverter

@Database(entities = [BirdNotes :: class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class, UUIDConverter::class)
abstract class BirdNoteDatabase : RoomDatabase() {
    abstract fun birdnoteDao() : BirdNoteDatabaseDao   //Dao = Data Access Object
}
package com.example.nestflix.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.nestflix.model.Settings

@Database(entities = [Settings :: class], version = 1, exportSchema = false)
abstract class SettingsDatabase : RoomDatabase() {
    abstract fun settingsDao() : SettingsDatabaseDao   //Dao = Data Access Object
}
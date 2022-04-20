package com.example.nestflix.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import com.example.nestflix.model.Settings

@Dao
interface SettingsDatabaseDao {

    @Query("SELECT * from settings_tbl")
   suspend fun getSettings(): List <Settings>

    @Delete
   suspend fun deleteSetting(setting: Settings)
}
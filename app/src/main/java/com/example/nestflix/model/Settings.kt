package com.example.nestflix.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.util.*

@Entity(tableName = "settings_tbl")
data class Settings(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),

    @ColumnInfo()
    val ipAddress : String,


)
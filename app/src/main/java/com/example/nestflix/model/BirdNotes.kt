package com.example.nestflix.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

@Entity(tableName = "videoCaptureNotes_tbl")
data class BirdNotes(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),

    @ColumnInfo()
    val pathToPicture : String,

    @ColumnInfo()
    val title: String,

    @ColumnInfo()
    val description : String,

    @ColumnInfo()
    val entryDate: Date = Date.from(Instant.now())
)

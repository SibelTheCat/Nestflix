package com.example.nestflix.data

import androidx.room.*
import com.example.nestflix.model.BirdNotes
import kotlinx.coroutines.flow.Flow

@Dao
interface BirdNoteDatabaseDao {

    @Query("SELECT * from videoCaptureNotes_tbl")
    fun getBirdNotes(): Flow <List<BirdNotes>>   //like mutable state of, asynchronous!!!!

    @Query("SELECT * from videoCaptureNotes_tbl where id = :id")
    suspend fun getBirdNoteById(id : String): BirdNotes

    @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(birdNote : BirdNotes)

    @Update(onConflict = OnConflictStrategy.REPLACE)
   suspend fun update(birdNotes: BirdNotes)

    @Query("DELETE from videoCaptureNotes_tbl")
   suspend fun deleteAll()

    @Delete
   suspend fun deleteNote(birdNote: BirdNotes)
}

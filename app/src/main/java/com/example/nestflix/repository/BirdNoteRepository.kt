package com.example.nestflix.repository

import com.example.nestflix.data.BirdNoteDatabaseDao
import com.example.nestflix.model.BirdNotes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

//needs access to DAO -> the connector to the data in the SQL
class BirdNoteRepository @Inject constructor(private val birdNoteDatabaseDao : BirdNoteDatabaseDao) {
    //operations get invoced

    suspend fun addBirdNote(birdNote : BirdNotes) = birdNoteDatabaseDao.insert(birdNote)
    suspend fun updateBirdNote(birdNote : BirdNotes) = birdNoteDatabaseDao.update(birdNote)
    suspend fun deleteBirdNote(birdNote : BirdNotes) = birdNoteDatabaseDao.deleteNote(birdNote)
    suspend fun deleteAllBirdNotes() = birdNoteDatabaseDao.deleteAll()
    fun getAllBirdNotes(): Flow<List<BirdNotes>> = birdNoteDatabaseDao.getBirdNotes().flowOn(
        Dispatchers.IO).conflate()


}
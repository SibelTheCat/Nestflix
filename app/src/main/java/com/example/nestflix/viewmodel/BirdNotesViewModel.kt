package com.example.nestflix.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.nestflix.model.BirdNotes

class BirdNotesViewModel : ViewModel() {

    private var birdNotesList = mutableStateListOf<BirdNotes>()


    init{
        birdNotesList.add(BirdNotes(pathToPicture = "dfasf", title = "Test 1", description = "this bird just sleeps all day"))
    }

    fun addBirdNote(birdNotes: BirdNotes){
        birdNotesList.add(birdNotes)
    }

    fun removeBirdNote(birdNotes: BirdNotes){
        birdNotesList.remove(birdNotes)
    }

    fun getAllBirdNotes(): List<BirdNotes>{
        return birdNotesList
    }
}
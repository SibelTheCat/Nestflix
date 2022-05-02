package com.example.nestflix.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.nestflix.model.BirdNotes

class BirdNotesViewModel : ViewModel() {

    private var birdNotesList = mutableStateListOf<BirdNotes>()


    init{
        birdNotesList.add(BirdNotes(pathToPicture = "/storage/emulated/0/Pictures/d043bbdf-a3ab-41c0-aa70-543fdad5b370.jpg", title = "Test 1", description = "this bird just sleeps all day"))
    }

    fun addBirdNote(birdNotes: BirdNotes){
        birdNotesList.add(birdNotes)
    }

    fun updateTitle(birdNotes: BirdNotes, newTitle : String){
        birdNotes.title = newTitle
    }

    fun updateDescription(birdNotes: BirdNotes, newDes : String){
        birdNotes.description = newDes
    }

    fun removeBirdNote(birdNotes: BirdNotes){
        birdNotesList.remove(birdNotes)
    }

    fun getAllBirdNotes(): List<BirdNotes>{
        return birdNotesList
    }
}
package com.example.nestflix.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nestflix.model.BirdNotes
import com.example.nestflix.repository.BirdNoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BirdNotesViewModel @Inject constructor(private val repository : BirdNoteRepository) : ViewModel() {

   // private var birdNotesList = mutableStateListOf<BirdNotes>()   -> hard to use mutable state with room
private val _birdNotesLists = MutableStateFlow<List<BirdNotes>>(emptyList())
    val birdNotesList = _birdNotesLists.asStateFlow()

    init{

        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllBirdNotes().distinctUntilChanged()
                .collect{ listOfBirdNotes ->
                    if (listOfBirdNotes.isNullOrEmpty()){
                        Log.d("Empty", ": Empty list")
                    } else {
                        _birdNotesLists.value = listOfBirdNotes
                    }

                }
        }
    }

    fun addBirdNote(birdNotes: BirdNotes) = viewModelScope.launch {repository.addBirdNote(birdNotes)}

    fun updateBirdnote(birdNotes: BirdNotes)= viewModelScope.launch { repository.updateBirdNote(birdNotes)}

    fun romoveBirdNotes(birdNotes: BirdNotes) = viewModelScope.launch {repository.deleteBirdNote(birdNotes)}

    fun updateTitle(birdNotes: BirdNotes, newTitle : String){
        birdNotes.title = newTitle
    }

    fun updateDescription(birdNotes: BirdNotes, newDes : String){
        birdNotes.description = newDes
    }



  //  fun getAllBirdNotes(): List<BirdNotes>{
    //    return birdNotesList
  //  }
}
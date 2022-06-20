package com.example.nestflix.screens.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.nestflix.model.BirdNotes
import com.example.nestflix.viewmodel.BirdNotesViewModel
import java.io.File

@Composable
fun DetailScreen(
    navController: NavController = rememberNavController(),
    path: String? = "",
    birdNoteViewModel: BirdNotesViewModel = viewModel(),
    birdnotelist: List<BirdNotes> = birdNoteViewModel.birdNotesList.collectAsState().value)
{ Scaffold(
topBar = {
    TopAppBar(backgroundColor = MaterialTheme.colors.primaryVariant, elevation = 3.dp) {
        Row {
            Icon(imageVector = Icons.Default.ArrowBack,
                contentDescription = "Arrow back",
                modifier = Modifier.clickable {
                    navController.popBackStack() //go back to last screen
                })

            Spacer(modifier = Modifier.width(20.dp))
        }

    }
}
) {
    Image(rememberAsyncImagePainter(File((getPath(id = path, birdNoteViewModel = birdNoteViewModel, birdnotelist = birdnotelist)).pathToPicture)),
        contentDescription = "bird screenshot",
        contentScale = ContentScale.Crop,
        modifier = Modifier

            .fillMaxWidth()
            .border(width = 2.dp, color = MaterialTheme.colors.secondary)




    )
}}

//get the Birdnote with passed id
@Composable
fun getPath(id : String?, birdNoteViewModel: BirdNotesViewModel = viewModel(),
                birdnotelist: List<BirdNotes> = birdNoteViewModel.birdNotesList.collectAsState().value): BirdNotes{
    return birdnotelist.filter{birdnote -> birdnote.id.toString() == id}[0]
}



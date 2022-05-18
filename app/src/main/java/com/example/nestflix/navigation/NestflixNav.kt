package com.example.nestflix.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nestflix.SettingsViewModelFactory
import com.example.nestflix.manager.SettingsDataStore
import com.example.nestflix.screens.gallery.GalleryScreen
import com.example.nestflix.screens.home.HomeScreen
import com.example.nestflix.screens.setup.SetUpScreen
import com.example.nestflix.screens.stream.StreamScreen
import com.example.nestflix.viewmodel.BirdNotesViewModel
import com.example.nestflix.viewmodel.MediaPlayerViewModel
import com.example.nestflix.viewmodel.SettingsViewModel

import org.videolan.libvlc.MediaPlayer

@Composable
fun NestflixNavigation(mediaPlayerViewModel : MediaPlayerViewModel = viewModel()){
    val navController = rememberNavController()
    val birdNoteViewModel: BirdNotesViewModel = viewModel()
    val settingsViewModel: SettingsViewModel = viewModel(
       factory = SettingsViewModelFactory(SettingsDataStore(LocalContext.current)))

    NavHost(navController = navController, startDestination = NestflixScreens.HomeScreen.name){
        composable(route = NestflixScreens.HomeScreen.name){ HomeScreen(navController = navController)}
        composable(route = NestflixScreens.SetupScreen.name){ SetUpScreen(navController = navController, settingsViewModel = settingsViewModel)}
        composable(route = NestflixScreens.StreamScreen.name){ StreamScreen(navController = navController, birdNoteViewModel= birdNoteViewModel, mediaPlayerViewModel = mediaPlayerViewModel, settingsViewModel = settingsViewModel)}
        composable(route = NestflixScreens.GalleryScreen.name){ GalleryScreen(navController = navController, birdNoteViewModel= birdNoteViewModel, birdnotelist =  birdNoteViewModel.birdNotesList.collectAsState().value)}
    }

}
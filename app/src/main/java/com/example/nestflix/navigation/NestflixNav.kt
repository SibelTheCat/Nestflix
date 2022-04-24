package com.example.nestflix.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nestflix.screens.gallery.GalleryScreen
import com.example.nestflix.screens.home.HomeScreen
import com.example.nestflix.screens.setup.SetUpScreen
import com.example.nestflix.screens.stream.StreamScreen
import org.videolan.libvlc.MediaPlayer

@Composable
fun NestflixNavigation(){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = NestflixScreens.HomeScreen.name){
        composable(route = NestflixScreens.HomeScreen.name){ HomeScreen(navController = navController)}
        composable(route = NestflixScreens.SetupScreen.name){ SetUpScreen(navController = navController)}
        composable(route = NestflixScreens.StreamScreen.name){ StreamScreen(navController = navController)}
        composable(route = NestflixScreens.GalleryScreen.name){ GalleryScreen(navController = navController)}
    }

}
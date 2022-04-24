package com.example.nestflix.screens.home


import android.content.res.Resources
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.*
import androidx.compose.material.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import com.example.nestflix.navigation.NestflixScreens
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.currentBackStackEntryAsState

@Preview(showBackground = true)
@Composable
fun HomeScreen(navController: NavController = rememberNavController()){

    Scaffold (

        topBar= {
            TopAppBar(title = {Text(text = "Nestflix")})

        },
                bottomBar = {
                   BottomNavigation( backgroundColor = MaterialTheme.colors.primary,
                   contentColor = Color.White) {
                       val navBackStackEntry by navController.currentBackStackEntryAsState()
                       val currentRoute = navBackStackEntry?.destination?.route

                       BottomNavigationItem(
                           icon = {Icon(imageVector = Icons.Default.Settings, contentDescription = "settings")},
                           label = { Text(text = "Settings") },
                           selected = currentRoute == NestflixScreens.SetupScreen.name,
                           onClick = { navController.navigate(route = NestflixScreens.SetupScreen.name) })

                       BottomNavigationItem(
                           icon = {Icon(imageVector = Icons.Default.Star, contentDescription = "gallery")},
                           label = { Text(text = "Gallery") },
                           selected = currentRoute == NestflixScreens.GalleryScreen.name,
                           onClick = { navController.navigate(route = NestflixScreens.GalleryScreen.name) })


                   }


                    }
            )

    {
        Column {

            Card(
                modifier = Modifier
                    .padding(top = 70.dp)
                    .padding(20.dp)
                    .fillMaxWidth()
                    .clickable { navController.navigate(route = NestflixScreens.StreamScreen.name) },
                shape = RoundedCornerShape(corner = CornerSize(15.dp)),
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Stream Video", fontSize = 30.sp)
                    Image(
                        painter = painterResource(id = com.example.nestflix.R.drawable.nestflixlogo),
                        contentDescription = "nestflixLogo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(170.dp)
                            .border(width = 2.dp, color = Color.Blue, CircleShape)


                    )
                }


            }
        }
    }

}
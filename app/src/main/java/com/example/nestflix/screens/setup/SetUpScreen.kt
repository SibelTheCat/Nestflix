package com.example.nestflix.screens.setup

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.nestflix.SettingsViewModelFactory
import com.example.nestflix.manager.SettingsDataStore
import com.example.nestflix.viewmodel.SettingsViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.launch

@Composable
fun SetUpScreen(navController: NavController = rememberNavController(),
                settingsViewModel: SettingsViewModel = viewModel(
                    factory = SettingsViewModelFactory(SettingsDataStore(LocalContext.current))
                )

){

    Scaffold(
        topBar = {
            TopAppBar(backgroundColor = MaterialTheme.colors.primaryVariant , elevation = 3.dp) {
                Row {
                    Icon(imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Arrow back",
                        modifier = Modifier.clickable {
                            navController.popBackStack() //go back to last screen
                        })

                    Spacer(modifier = Modifier.width(20.dp))
                    Text(text = "Settings")
                }

            }
        }
    ) {

DataStoreInput(settingsViewModel)

    }
}


//thanks to https://www.youtube.com/watch?v=4D8dVPeWxIs,
//https://github.com/MakeItEasyDev/Jetpack-Compose-Preference-DataStore/blob/main/app/src/main/java/com/jetpack/datastorepreference/MainActivity.kt
@Composable
fun DataStoreInput(
    settingsViewModel: SettingsViewModel
) {
    val context = LocalContext.current
    val settingsDataStore: SettingsDataStore = SettingsDataStore.getInstance(context)
    val textState = remember { mutableStateOf(TextFieldValue()) }
    val getIPAddress = settingsViewModel.ipAddress.observeAsState().value
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.primaryVariant)
                .height(50.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Streaming address",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            OutlinedTextField(
                value = textState.value,
                onValueChange = { textState.value = it },
                label = { Text(text = "Please enter the streaming address of your raspberryPi", fontSize = 15.sp) },
                modifier = Modifier.fillMaxWidth(0.7f),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    scope.launch {
                        settingsViewModel.saveIpAddress(textState.value.text)
                    }
                },
                shape = RoundedCornerShape(8.dp),
                elevation = ButtonDefaults.elevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 8.dp,
                    disabledElevation = 0.dp
                ),
                modifier = Modifier.padding(5.dp)
            ) {
                Text(
                    text = "Save",
                    modifier = Modifier.padding(6.dp)
                )
            }

            //Showing the datastore value
            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Your saved IP Address is:\r\n ${getIPAddress!!}",
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontSize = 20.sp
            )
        }
    }
    }



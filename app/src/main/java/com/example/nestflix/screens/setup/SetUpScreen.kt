package com.example.nestflix.screens.setup

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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun SetUpScreen(navController: NavController = rememberNavController()){

    var ipAddress by remember {
        mutableStateOf("")
    }

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
        Column(){
            AddIPAdress(ipAddress = ipAddress, textGetsEntered = {
                if (it.all {char ->
                        char.isDigit() //|| char.equals(".")
                    })
                        ipAddress = it
            })
            SettingsButton(Modifier.padding(5.dp ), text = "safe", onClick = { /*TODO*/ })
        }



    }



}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddIPAdress(
    ipAddress : String,
    textGetsEntered : (String) -> Unit,
    onImeAction: ()-> Unit = {}
) {

    val keyboaardController = LocalSoftwareKeyboardController.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),

        shape = RoundedCornerShape(corner = CornerSize(15.dp)),
        elevation = 4.dp
    ) {
        Row() {
            TextField(value = ipAddress,
            onValueChange = textGetsEntered,
                label = {Text(text ="Please enter the IP-address")},
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    onImeAction()
                    keyboaardController?.hide()
                })
            )

        }
    }
}

@Composable
fun SettingsButton(
modifier : Modifier = Modifier,
text : String,
onClick: ()->Unit,
enabled: Boolean = true
){
    Button(onClick = onClick,
    shape = CircleShape,
    enabled = enabled,
    modifier = modifier){
        Text(text)

    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Preview (showBackground = true)
@Composable
fun SetUpScreenPreview(){
    SetUpScreen()
}
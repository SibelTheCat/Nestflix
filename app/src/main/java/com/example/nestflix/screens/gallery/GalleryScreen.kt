package com.example.nestflix.screens.gallery

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.nestflix.model.BirdNotes
import com.example.nestflix.viewmodel.BirdNotesViewModel
import com.kpstv.compose.kapture.attachController
import java.io.File


@Composable
fun GalleryScreen(
    navController: NavController = rememberNavController(),
    birdNoteViewModel: BirdNotesViewModel = viewModel(),
    birdnotelist: List<BirdNotes> = birdNoteViewModel.birdNotesList.collectAsState().value,
) {

    Scaffold(
        topBar = {
            TopAppBar(backgroundColor = MaterialTheme.colors.primaryVariant, elevation = 3.dp) {
                Row {
                    Icon(imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Arrow back",
                        modifier = Modifier.clickable {
                            navController.popBackStack() //go back to last screen
                        })

                    Spacer(modifier = Modifier.width(20.dp))
                    Text(text = "Gallery")
                }

            }
        }
    ) {
        LazyColumn {
            items(birdnotelist) { bird ->
                DisplayBirdNote(bird, birdNoteViewModel = birdNoteViewModel)
            }
        }

    }
}

@Composable
fun DisplayBirdNote(
    birdNotes: BirdNotes,
    birdNoteViewModel: BirdNotesViewModel = viewModel(),
) {

    val openDialog = remember { mutableStateOf(false) }
    var description by remember { mutableStateOf(birdNotes.description) }
    var title by remember { mutableStateOf(birdNotes.title) }
    Log.e("path", birdNotes.pathToPicture)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        // .clickable { },

        shape = RoundedCornerShape(corner = CornerSize(15.dp)),
        elevation = 4.dp
    ) {
        Row(Modifier.padding(8.dp)) {

            //https://stackoverflow.com/questions/70825508/load-local-image-with-jetpack-compose
            Image(rememberAsyncImagePainter(File(birdNotes.pathToPicture)),
                contentDescription = "bird screenshot",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(200.dp, 150.dp)
                    //.size(170.dp)
                    .border(width = 2.dp, color = MaterialTheme.colors.secondary)
            )

            Column(modifier = Modifier
                .align(alignment = Alignment.Top)
                .padding(5.dp)) {


                Text(
                    text = "${birdNotes.title}",
                    fontSize = 20.sp,
                    style = MaterialTheme.typography.body2,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(top = 10.dp, end = 10.dp)
                )
                Text(buildAnnotatedString {
                    var datealterd = birdNotes.entryDate.toString().replace("GMT+02:00", "").drop(4)

                    withStyle(style = SpanStyle(color = androidx.compose.ui.graphics.Color.Blue,
                        fontSize = 12.sp)) {
                        append("added: ")
                    }
                    withStyle(style = SpanStyle(fontSize = 14.sp)) {
                        append("${datealterd}")
                    }

                }

                )

                // Text(text = "${birdNotes.description}")
                //Das remember geht hier nicht?

                ExpandingText(birdNotes.description, maxExLines = 6)
                Spacer(Modifier.size(5.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    OutlinedButton(
                        border = BorderStroke(width = 1.dp, color = MaterialTheme.colors.secondary),
                        onClick = { openDialog.value = true },
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.defaultMinSize(minWidth = ButtonDefaults.MinWidth,
                            minHeight = 12.dp)

                    ) {
                        Text("Edit", fontSize = 10.sp)
                    }
                    Spacer(Modifier.size(35.dp))
                    IconButton(onClick = {
                        birdNoteViewModel.romoveBirdNotes(birdNotes)

                    },
                        modifier = Modifier.then(Modifier.size(24.dp))) {
                        Icon(
                            Icons.Filled.Delete,
                            tint = MaterialTheme.colors.primaryVariant,
                            contentDescription = "Trash Button",

                            )
                    }
                }


            }
        }
        if (openDialog.value) {
            AlertDialog(
                onDismissRequest = {
                    openDialog.value = false
                },
                title = {
                    Text(text = "Edit Picture description")
                },
                text = {
                    Column() {
                        Spacer(Modifier.size(5.dp))
                        TextField(
                            value = title,
                            onValueChange = {
                                title = it

                            }
                        )
                        Spacer(Modifier.size(5.dp))
                        TextField(
                            value = description,
                            onValueChange = {
                                description = it
                            }
                        )
                    }
                },
                buttons = {
                    Column(
                        modifier = Modifier.padding(all = 8.dp),
                        //horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                openDialog.value = false

                                birdNoteViewModel.updateBirdnote(BirdNotes(birdNotes.id,
                                    birdNotes.pathToPicture,
                                    title = title,
                                    description = description,
                                    birdNotes.entryDate))

                            }
                        ) {
                            Text("Save")
                        }
                        Spacer(Modifier.size(5.dp))
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { openDialog.value = false }
                        ) {
                            Text("Cancel")
                        }
                    }
                }
            )
        }
    }
}

//https://proandroiddev.com/expandabletext-in-jetpack-compose-b924ea424774
@Composable
fun ExpandingText(text: String, maxExLines: Int) {
    Log.d("fun", text)

    var isExpanded by remember { mutableStateOf(false) }

    val textLayoutResultState = remember { mutableStateOf<TextLayoutResult?>(null) }
    var isClickable by remember { mutableStateOf(false) }

    var finalText by remember { mutableStateOf(text) }

    //diese Zuweisung muss gemacht werden, sonst wird der Description Block nicht neu gerendert
    finalText = text
    Log.d("fun", finalText)


    val textLayoutResult = textLayoutResultState.value
    LaunchedEffect(textLayoutResult) {
        if (textLayoutResult == null) return@LaunchedEffect

        when {
            isExpanded -> {
                finalText = "$text ... Show Less"
            }
            !isExpanded && textLayoutResult.hasVisualOverflow -> {
                val lastCharIndex = textLayoutResult.getLineEnd(maxExLines - 1)
                val showMoreString = "... Show More"
                val adjustedText = text
                    .substring(startIndex = 0, endIndex = lastCharIndex)
                    .dropLast(showMoreString.length)
                    .dropLastWhile { it == ' ' || it == '.' }

                finalText = "$adjustedText$showMoreString"

                isClickable = true
            }
        }
    }

    Text(
        text = finalText,
        maxLines = if (isExpanded) Int.MAX_VALUE else maxExLines,
        onTextLayout = { textLayoutResultState.value = it },
        modifier = Modifier
            .clickable(enabled = isClickable) { isExpanded = !isExpanded }
            .animateContentSize()
            .padding(top = 5.dp),
        fontSize = 12.sp,

        )
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    //DisplayBirdNote(birdNotes = BirdNotes(pathToPicture = "/storage/emulated/0/Pictures/1270a526-3a44-4a07-a42e-9ad25039f8c6.jpg", title = "Test 1", description = "this bird just sleeps all day"), birdNoteViewModel = viewModel())
    val openDialog = remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),


        shape = RoundedCornerShape(corner = CornerSize(15.dp)),
        elevation = 4.dp
    ) {
        Row(Modifier.padding(8.dp)) {

            //https://stackoverflow.com/questions/70825508/load-local-image-with-jetpack-compose
            Image(rememberAsyncImagePainter(File("/storage/emulated/0/Pictures/1270a526-3a44-4a07-a42e-9ad25039f8c6.jpg")),
                contentDescription = "bird screenshot",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(200.dp, 150.dp)
                    //.size(170.dp)
                    .border(width = 2.dp, color = MaterialTheme.colors.secondary)
            )

            Column(modifier = Modifier
                .align(alignment = Alignment.Top)
                .padding(5.dp)) {


                Text(
                    text = "titel",
                    fontSize = 20.sp,
                    style = MaterialTheme.typography.body2,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(top = 10.dp, end = 10.dp)
                )
                Text(buildAnnotatedString {
                    //   var datealterd = birdNotes.entryDate.toString().replace("GMT+02:00", "").drop(4)

                    withStyle(style = SpanStyle(color = androidx.compose.ui.graphics.Color.Blue,
                        fontSize = 12.sp)) {
                        append("added: ")
                    }
                    withStyle(style = SpanStyle(fontSize = 14.sp)) {
                        append("heute")
                    }

                }

                )

                // Text(text = "${birdNotes.description}")
                //Das remember geht hier nicht?

                // ExpandingText(birdNotes.description, maxExLines =6 )
                Spacer(Modifier.size(5.dp))

                Row(verticalAlignment = Alignment.Bottom

                ) {
                    OutlinedButton(
                        border = BorderStroke(width = 1.dp, color = MaterialTheme.colors.secondary),
                        onClick = { openDialog.value = true },
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.defaultMinSize(minWidth = ButtonDefaults.MinWidth,
                            minHeight = 12.dp)

                    ) {
                        Text("Edit", fontSize = 10.sp)
                    }
                    Spacer(Modifier.size(25.dp))
                    IconButton(onClick = {
                        //  birdNoteViewModel.romoveBirdNotes(birdNotes)
                    },
                        //https://www.androidbugfix.com/2022/05/android-jetpack-compose-iconbutton.html
                        modifier = Modifier.then(Modifier.size(24.dp))) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Trash Button",
                            modifier = Modifier.align(Alignment.Bottom)
                        )
                    }
                }

            }
        }
        if (openDialog.value) {
            AlertDialog(
                onDismissRequest = {
                    openDialog.value = false
                },
                title = {
                    Text(text = "Edit Picture description")
                },
                text = {
                    Column() {
                        Spacer(Modifier.size(5.dp))
                        TextField(
                            value = "title",
                            onValueChange = {

                            }
                        )
                        Spacer(Modifier.size(5.dp))
                        TextField(
                            value = "description",
                            onValueChange = {
                            }
                        )
                    }
                },
                buttons = {
                    Column(
                        modifier = Modifier.padding(all = 8.dp),
                        //horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                openDialog.value = false

                                //   birdNoteViewModel.updateBirdnote(BirdNotes(birdNotes.id, birdNotes.pathToPicture, title = title, description = description, birdNotes.entryDate))

                            }
                        ) {
                            Text("Save")
                        }
                        Spacer(Modifier.size(5.dp))
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { openDialog.value = false }
                        ) {
                            Text("Cancel")
                        }
                    }
                }
            )
        }
    }
}

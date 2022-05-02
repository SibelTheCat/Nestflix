package com.example.nestflix.screens.gallery

import android.R.attr.maxLines
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.example.nestflix.R
import com.example.nestflix.model.BirdNotes
import com.example.nestflix.viewmodel.BirdNotesViewModel
import java.io.File


@Composable
fun GalleryScreen(navController: NavController = rememberNavController(),
                  birdNoteViewModel: BirdNotesViewModel = viewModel(),
                  birdnotelist: List<BirdNotes> = birdNoteViewModel.getAllBirdNotes()) {

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

}}

@Composable
fun DisplayBirdNote(birdNotes: BirdNotes,
                    birdNoteViewModel: BirdNotesViewModel = viewModel()) {

    val openDialog = remember { mutableStateOf(false) }
    var text by remember { mutableStateOf(birdNotes.description) }
    var title by remember { mutableStateOf(birdNotes.title) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            //damit wenn Textfeld ausgefahren wird die Card größer ist
            // .animateContentSize()

            //wenn auf die Card geklickt wird, wird die übergebene Funkton "onItemClick"
            // mit der movie id auf des geklckten Films aufgerufen
            .clickable { },

        shape = RoundedCornerShape(corner = CornerSize(15.dp)),
        elevation = 4.dp
    ) {
        Row(Modifier.padding(8.dp)) {

           //https://stackoverflow.com/questions/70825508/load-local-image-with-jetpack-compose
            Image(rememberAsyncImagePainter(File(birdNotes.pathToPicture)),
                contentDescription = "bird screenshot",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(150.dp,250.dp)
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
                    modifier = Modifier.padding( top= 10.dp, end = 10.dp)
                )
                Text(buildAnnotatedString {
                        withStyle(style = SpanStyle(color = androidx.compose.ui.graphics.Color.Blue, fontSize = 12.sp)) {
                            append("added: ")
                        }
                        append("${birdNotes.entryDate}")

                }

                )

              ExpandingText(text = birdNotes.description, maxExLines =6 )
                Spacer(Modifier.size(5.dp))
               OutlinedButton(
                   border = BorderStroke(width = 1.dp,color = MaterialTheme.colors.secondary),
                    onClick = { openDialog.value = true }
                ) {
                    Text("Edit",  fontSize = 10.sp,)
                }
              /*  old version ->
                var textOverflow by remember { mutableStateOf(false) }
                Text(
                    text = birdNotes.description,
                    style = MaterialTheme.typography.overline,
                    maxLines = 6,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 5.dp),
                    onTextLayout = { textLayoutResult ->
                        textOverflow = textLayoutResult.hasVisualOverflow
                    },
                )

               if(textOverflow) {
                    Button(onClick = {}) {
                        Text("show more")
                   }
                }*/

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
                            onValueChange = { title = it
                           birdNoteViewModel.updateTitle(birdNotes = birdNotes, newTitle = title )

                                }
                        )
                        Spacer(Modifier.size(5.dp))
                        TextField(
                            value = text,
                            onValueChange = { text = it
                                birdNoteViewModel.updateDescription(birdNotes = birdNotes, newDes = text )}
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
                            onClick = { openDialog.value = false }
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

@Composable
fun ExpandingText(text: String, maxExLines : Int) {
    var isExpanded by remember { mutableStateOf(false) }
    val textLayoutResultState = remember { mutableStateOf<TextLayoutResult?>(null) }
    var isClickable by remember { mutableStateOf(false) }
    var finalText by remember { mutableStateOf(text) }

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
    DisplayBirdNote(birdNotes = BirdNotes(pathToPicture = "dfasf", title = "Test 1", description = "this bird just sleeps all day hjghghjgjhgjkhghjghjkghjgjghghghjghghjghjghjghjghjghjghghjghjghjghjghjghjgjgkjghj   jkhj hj k hjhj jhj   jhj jh  hjhl jh jhjgjhghgj jgjkgjgjgjk hjkhjlkhkhkhhjjj  jhkljhjhjkh  jhjkhjkh  kjhjkhjkhkjl kjhkjlhjkh jhjkhjkhjkhjkh hjhjkhjhjhlk hjkh jh h jh jh jhjlh  jhkjhjkhkjhlkjhlj khjlhlkhlkjh jhjhjh jhlhj"))
}
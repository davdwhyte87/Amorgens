package com.amorgens.feed.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.amorgens.NavScreen
import com.amorgens.feed.data.FeedViewModel
import com.amorgens.feed.domain.CreatePostReq
import com.amorgens.ui.GeneralScaffold


@Composable
fun createPostScreen(navController: NavController, feedViewModel: FeedViewModel){
    DisposableEffect(true) {
        onDispose {
            feedViewModel.clearModelData()
        }
    }
    val postText = remember {
        mutableStateOf("")
    }
    val feedUIState = feedViewModel.feedUIState.collectAsState()
    if(feedUIState.value.isCreatePostError){
        Toast.makeText(LocalContext.current, feedUIState.value.createPostErrorMessage, Toast.LENGTH_SHORT).show()
        feedViewModel.clearModelData()
    }

    if(feedUIState.value.isCreatePostSuccess){
        Toast.makeText(LocalContext.current, "Post Created!", Toast.LENGTH_SHORT).show()
        feedViewModel.clearModelData()
        //navController.popBackStack()
    }

    GeneralScaffold(
        topBar = { createPostNav(navController,
            {
                feedViewModel.createPost(CreatePostReq(text = postText.value))

        }, feedViewModel) }, floatingActionButton = { /*TODO*/ }) {

        TextField(value = postText.value,
            onValueChange = { postText.value = it},
            maxLines = 20,
            modifier = Modifier.fillMaxSize(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            placeholder = { Text(
                text = "What are you thinking?",
                color = Color.Gray,

            )}
        )
    }
}
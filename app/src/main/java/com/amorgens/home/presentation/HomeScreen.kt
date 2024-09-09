package com.amorgens.home.presentation

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.amorgens.NavScreen
import com.amorgens.feed.data.FeedViewModel
import com.amorgens.feed.presentation.FeedScreen
import com.amorgens.general.utils.CLog
import com.amorgens.general.utils.clog
import com.amorgens.home.presentation.components.BottomNavItem
import com.amorgens.menu.presentation.MenuScreen
import com.amorgens.ui.theme.Purple
import io.sentry.Sentry
import kotlinx.coroutines.launch

// home screen will have buttom navigation and will contains four screens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, feedViewModel: FeedViewModel){
    val systemData = feedViewModel.systemData.collectAsState()
    DisposableEffect(true) {
        // get the app version
        feedViewModel.getSystemData()

        onDispose {

        }
    }

    val context = LocalContext.current
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    val versionName = packageInfo.versionName

    val showUpdateModal = remember {
        mutableStateOf(false)
    }

    if (systemData.value.android_app_version != versionName ){
        CLog.debug("NEW APP VERSION", "YEs")
        showUpdateModal.value = true
    }else{
        showUpdateModal.value = false
    }
    CLog.debug("SYSTEM DATA", systemData.value.android_app_version )
    CLog.error("APP VERSION",versionName )
    //clog("EAT ME", " YUM YUM")
    //Sentry.captureException(RuntimeException("This app uses Sentry! :)"))
    var selectedTabIndex by remember {
        mutableIntStateOf(0)
    }

    val sheetState = rememberModalBottomSheetState()
    val coroutine = rememberCoroutineScope()
    val navItems = listOf(
        BottomNavItem(
            title = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            hasNews = false,
            badgeCount = 0,
            route =""
        ),
        BottomNavItem(
            title = "Chat",
            selectedIcon = Icons.Filled.ChatBubble,
            unselectedIcon = Icons.Outlined.ChatBubbleOutline,
            hasNews = false,
            badgeCount = 0,
            route = ""
        ),
        BottomNavItem(
            title = "Profile",
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person,
            hasNews = false,
            badgeCount = 0,
            route = ""
        ),
        BottomNavItem(
            title = "Menu",
            selectedIcon = Icons.Filled.Menu,
            unselectedIcon = Icons.Outlined.Menu,
            hasNews = false,
            badgeCount = 0,
            route = ""
        ),
        )
    val pagerState: PagerState = rememberPagerState {
        navItems.size
    }

    LaunchedEffect(selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }

    LaunchedEffect(pagerState.currentPage) {
        if (!pagerState.isScrollInProgress) {
            selectedTabIndex = pagerState.currentPage
        }
    }
    Column {
        HorizontalPager(state = pagerState, modifier= Modifier
            .fillMaxWidth()
            .weight(1f)) {
            if(it == 0){
                FeedScreen(navController, feedViewModel)
            }
            if(it == 1){
                chatScreen()
            }
            if(it == 2){
                profileScreen()
            }
            if(it == 3){
                MenuScreen(navController)
            }
        }
        TabRow(selectedTabIndex = selectedTabIndex) {
            navItems.forEachIndexed { index, bottomNavItem ->
                Tab(selected = index==selectedTabIndex,
                    onClick = {selectedTabIndex = index},
                    icon = {
                        Icon(
                            imageVector = if (selectedTabIndex == index){bottomNavItem.selectedIcon}else{bottomNavItem.unselectedIcon},
                            contentDescription = bottomNavItem.title,
                            tint = Purple
                        )
                    }
                )
            }
        }



        if (showUpdateModal.value){
            ModalBottomSheet(
                onDismissRequest = { coroutine.launch { sheetState.show() }},
                sheetState = sheetState,
            ) {
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(50.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {

                    // text
                    Text(text = "There is a new version of the app, please update!",
                        style = MaterialTheme.typography.titleLarge
                    )
                    // signup button
                    Button(onClick = {},
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.size(width = 200.dp, height = 50.dp)
                    ) {
                        Text(text = "Update App",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                }
            }
        }
    }
}
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}


@Composable
fun chatScreen(){
    Text(
        text = "Chat feature coming soon ",

    )
}


@Composable
fun profileScreen(){
    Text(
        text = "Profile feature coming soon ",

        )
}

package com.amorgens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.amorgens.auth.data.AuthViewModel
import com.amorgens.feed.data.FeedViewModel
import com.amorgens.home.presentation.HomeScreen
import com.amorgens.trade.data.OrderViewModel
import com.amorgens.ui.theme.AmorgensTheme
import com.amorgens.wallet.data.WalletViewModel
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import io.sentry.SentryLevel
import io.sentry.android.core.SentryAndroid


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        SentryAndroid.init(this) { options ->
            options.dsn = "https://e5aa33217ae0e9e465a28c7f3cbc0a45@o4507910790119424.ingest.us.sentry.io/4507910882131968"
            options.tracesSampleRate = 1.0 // Set the performance monitoring sample rate
            options.setDiagnosticLevel(SentryLevel.ERROR)
            options.isDebug = true
            options.isEnableUncaughtExceptionHandler = true
        }


        setContent {
            val walletViewModel:WalletViewModel = hiltViewModel()
            val orderViewModel:OrderViewModel = hiltViewModel()
            val authViewModel:AuthViewModel = hiltViewModel()
            val feedViewModel:FeedViewModel = hiltViewModel()
            val navController = rememberNavController()
            AmorgensTheme(darkTheme = false) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNav(navController =navController,
                        walletViewModel ,
                        orderViewModel,
                        authViewModel,
                        feedViewModel
                    )

                }
            }
        }
    }



}




//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    AmorgensTheme {
//        Greeting("Android")
//    }
//}

//amara101 12345
// drake_drizy
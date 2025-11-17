package com.easybraille.ui

import android.graphics.Color
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.easybraille.ui.screens.AccountScreen
import com.easybraille.ui.screens.CameraScreen
import com.easybraille.ui.screens.HistoryScreen
import com.easybraille.ui.screens.RegisterScreen
import com.easybraille.ui.screens.TranslatorScreen
import com.easybraille.ui.screens.WelcomeScreen
import com.easybraille.ui.screens.LoginScreen
import com.easybraille.ui.theme.EsyBrailleAPPTheme
import com.easybraille.ui.utils.currentWindowType
import com.easybraille.ui.utils.WindowType
import com.easybraille.ui.utils.AuthManager
import java.util.*


const val ROOT_ROUTE = "auth_root"

class MainActivity : ComponentActivity() {
    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        super.onCreate(savedInstanceState)

        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale.forLanguageTag("es-MX")
            }
        }

        setContent {
            EsyBrailleAPPTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val windowType = currentWindowType()
                    AppNavHost(tts = tts, windowType = windowType)
                }
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        tts.shutdown()
    }
}

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    tts: TextToSpeech,
    windowType: WindowType
) {
    val context = LocalContext.current

    val startDestination = if (AuthManager.isLoggedIn(context)) "home" else ROOT_ROUTE
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        navigation(
            startDestination = "welcome",
            route = ROOT_ROUTE
        ) {
            composable("welcome") { WelcomeScreen(navController, windowType) }
            composable("login") { LoginScreen(navController, windowType) }
            composable("register") { RegisterScreen(navController, windowType) }
        }

        composable("home") { TranslatorScreen(navController) }
        composable("history") { HistoryScreen(navController) }
        composable("camera") { CameraScreen(navController, windowType) }
        composable("account") { AccountScreen(navController) }

    }
}

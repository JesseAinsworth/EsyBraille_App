package com.example.esybrailleapp

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.esybrailleapp.ui.screens.RegisterScreen
import com.example.esybrailleapp.ui.LoginScreen
import com.example.esybrailleapp.ui.screens.*
import com.example.esybrailleapp.ui.theme.EsyBrailleAPPTheme
import java.util.*

class MainActivity : ComponentActivity() {
    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale("es", "MX")
            }
        }

        setContent {
            EsyBrailleAPPTheme {
                val navController: NavHostController = rememberNavController()

                NavHost(navController = navController, startDestination = "splash") {
                    composable("splash") { SplashScreen(navController) }
                    composable("welcome") { WelcomeScreen(navController) }
                    composable("login") { LoginScreen(navController) }
                    composable("register") { RegisterScreen(navController) }
                    composable("home") { TranslatorScreen(tts, navController) }
                    composable("camera") { CameraScreen(navController) }
                    composable("voice") { VoiceScreen(navController) }
                    composable("library") { LibraryScreen(navController) }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tts.shutdown()
    }
}

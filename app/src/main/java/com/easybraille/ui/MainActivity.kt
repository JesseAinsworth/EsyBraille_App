package com.easybraille.ui

import android.graphics.Color
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.easybraille.ui.screens.*
import com.easybraille.ui.theme.AppTheme
import com.easybraille.ui.utils.currentWindowType
import com.easybraille.ui.utils.WindowType
import com.easybraille.ui.utils.AuthManager
import com.easybraille.utils.ThemeManager
import com.esybraille.ui.screens.WelcomeScreen
import comesybraille.ui.screens.RegisterScreen

import java.util.*

const val ROOT_ROUTE = "auth_root"

class MainActivity : ComponentActivity() {
    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        ThemeManager.init(this)

        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale.forLanguageTag("es-MX")
            }
        }

        setContent {
            val themeMode by ThemeManager.currentTheme.collectAsState()
            val systemInDark = isSystemInDarkTheme()

            val useDarkTheme = when (themeMode) {
                1 -> true
                0 -> false
                else -> systemInDark
            }

            // Configuración de barras
            val barStyle = if (useDarkTheme) {
                SystemBarStyle.dark(Color.TRANSPARENT)
            } else {
                SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
            }

            enableEdgeToEdge(
                statusBarStyle = barStyle,
                navigationBarStyle = barStyle
            )

            // Llamada al tema con el nombre y parámetros correctos
            AppTheme(darkTheme = useDarkTheme) {
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
    val context = androidx.compose.ui.platform.LocalContext.current
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

        composable("home") { TranslatorScreen(tts, navController, windowType) }
        composable("history") { HistoryScreen(navController) }
        composable("camera") { CameraScreen(navController, windowType) }
        composable("account") { AccountScreen(navController) }
        composable("theme_settings") { ThemeSettingsScreen(navController) }
    }
}
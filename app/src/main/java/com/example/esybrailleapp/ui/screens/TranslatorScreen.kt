package com.example.esybrailleapp.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.esybrailleapp.R
import com.example.esybrailleapp.ROOT_ROUTE
import com.example.esybrailleapp.network.ApiClient
import com.example.esybrailleapp.network.ApiService
import com.example.esybrailleapp.network.TranslationRequest
import com.example.esybrailleapp.network.TranslationResponse
import com.example.esybrailleapp.ui.theme.*
import com.example.esybrailleapp.ui.utils.WindowType
import com.example.esybrailleapp.utils.AuthManager
import com.example.esybrailleapp.utils.saveBraillePdf
import kotlinx.coroutines.delay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

private val brailleMap: Map<Char, String> = mapOf(
    'a' to "⠁", 'b' to "⠃", 'c' to "⠉", 'd' to "⠙", 'e' to "⠑", 'f' to "⠋", 'g' to "⠛", 'h' to "⠓",
    'i' to "⠊", 'j' to "⠚", 'k' to "⠅", 'l' to "⠇", 'm' to "⠍", 'n' to "⠝", 'o' to "⠕", 'p' to "⠏",
    'q' to "⠟", 'r' to "⠗", 's' to "⠎", 't' to "⠞", 'u' to "⠥", 'v' to "⠧", 'w' to "⠺", 'x' to "⠭",
    'y' to "⠽", 'z' to "⠵", 'ñ' to "⠟",'á' to "⠷", 'é' to "⠮", 'í' to "⠌", 'ó' to "⠬", 'ú' to "⠾",
    'ü' to "⠳", '1' to "⠼⠁", '2' to "⠼⠃", '3' to "⠼⠉", '4' to "⠼⠙", '5' to "⠼⠑", '6' to "⠼⠋",
    '7' to "⠼⠛", '8' to "⠼⠓", '9' to "⠼⠊", '0' to "⠼⠚", '.' to "⠲", ',' to "⠂", ';' to "⠆",
    ':' to "⠒", '!' to "⠖", '(' to "⠶", ')' to "⠶", ' ' to " ", '+' to "⠖", '-' to "⠤",
    '*' to "⠦", '/' to "⠌"
)

private fun translateToBraille(text: String): String {
    val lowercasedText = text.lowercase(Locale.ROOT)
    val stringBuilder = StringBuilder()
    for (char in lowercasedText) {
        stringBuilder.append(brailleMap[char] ?: char)
    }
    return stringBuilder.toString()
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ServiceCast")
@Composable
fun TranslatorScreen(
    tts: TextToSpeech,
    navController: NavHostController,
    windowType: WindowType
) {
    var inputText by remember { mutableStateOf("") }
    var brailleText by remember { mutableStateOf("") }
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }

    val spanishBorderColor = Color(0xFF87CEEB)
    val darkSpanishBorderColor = Color(0xFF005f88)
    val brailleBorderColor = Color(0xFFFF6347)
    val darkBrailleBorderColor = Color(0xFF8B0000)

    val spanishBorderBrush = Brush.verticalGradient(
        colors = listOf(darkSpanishBorderColor, spanishBorderColor)
    )
    val brailleBorderBrush = Brush.verticalGradient(
        colors = listOf(darkBrailleBorderColor, brailleBorderColor)
    )

    LaunchedEffect(inputText) {
        delay(300)
        brailleText = if (inputText.isNotBlank()) {
            translateToBraille(inputText)
        } else {
            ""
        }
    }

    val speechRecognitionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val results: ArrayList<String>? = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!results.isNullOrEmpty()) {
                inputText = results[0]
            }
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Habla ahora...")
            }
            speechRecognitionLauncher.launch(intent)
        } else {
            Toast.makeText(context, "Permiso de micrófono denegado", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { /* Vacío para un look limpio */ },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent),
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menú de opciones")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Mi Cuenta") },
                            onClick = {
                                showMenu = false
                                navController.navigate("account")
                            },
                            leadingIcon = { Icon(Icons.Default.AccountCircle, "Cuenta") }
                        )
                        DropdownMenuItem(
                            text = { Text("Salir") },
                            onClick = {
                                showMenu = false
                                AuthManager.setLoggedIn(context, false)
                                navController.navigate(ROOT_ROUTE) {
                                    popUpTo(0)
                                }
                            },
                            leadingIcon = { Icon(Icons.AutoMirrored.Outlined.ExitToApp, "Salir") }
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.easybrailleblanco),
                contentDescription = "Logo de la App",
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Traducir",
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Traduciendo ideas",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, brush = spanishBorderBrush, shape = RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    TextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        placeholder = { Text("Introduzca el texto que desea traducir", color = TextSecondary) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = TextPrimary,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Español", color = TextSecondary, fontWeight = FontWeight.Bold)
                        IconButton(onClick = { permissionLauncher.launch(Manifest.permission.RECORD_AUDIO) }) {
                            Icon(Icons.Default.Mic, contentDescription = "Entrada de voz", tint = TextPrimary)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, brush = brailleBorderBrush, shape = RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = brailleText,
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        color = TextPrimary
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Braille", color = TextSecondary, fontWeight = FontWeight.Bold)
                        Row {
                            IconButton(onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Traducción Braille", brailleText)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Copiado al portapapeles", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copiar", tint = TextPrimary)
                            }

                            // --- BOTÓN GUARDAR E IMPRIMIR ---
                            IconButton(onClick = {
                                if (brailleText.isNotBlank()) {
                                    // 1. Guardar PDF localmente
                                    val savedFile = saveBraillePdf(context, inputText, brailleText)

                                    if (savedFile != null) {
                                        Toast.makeText(context, "Guardado en historial", Toast.LENGTH_SHORT).show()

                                        // 2. Enviar datos al Backend
                                        val service = ApiClient.instance.create(ApiService::class.java)
                                        val request = TranslationRequest(
                                            spanishText = inputText,
                                            brailleText = brailleText
                                        )
                                        service.saveTranslation(request).enqueue(object : Callback<TranslationResponse> {
                                            override fun onResponse(call: Call<TranslationResponse>, response: Response<TranslationResponse>) {
                                                if (response.isSuccessful) {
                                                    Toast.makeText(context, "Guardado en la nube", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                            override fun onFailure(call: Call<TranslationResponse>, t: Throwable) {
                                                // Opcional: Manejar error silenciosamente
                                            }
                                        })

                                    } else {
                                        Toast.makeText(context, "Error al guardar", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(context, "No hay texto que guardar", Toast.LENGTH_SHORT).show()
                                }
                            }) {
                                Icon(Icons.Default.Print, contentDescription = "Guardar en Historial", tint = TextPrimary)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FeatureButton(
                    icon = Icons.Default.CameraAlt,
                    text = "Cámara",
                    backgroundColor = BlueAccent,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate("camera") }
                )
                FeatureButton(
                    icon = Icons.Default.History,
                    text = "Historial",
                    backgroundColor = GreenAccent,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate("history") }
                )
            }
        }
    }
}

@Composable
fun FeatureButton(
    icon: ImageVector,
    text: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(90.dp)
            .clickable(onClick = onClick)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = backgroundColor,
                ambientColor = backgroundColor
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = TextPrimary,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = text,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
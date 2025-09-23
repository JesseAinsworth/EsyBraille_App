package com.example.esybrailleapp.ui.screens

import android.content.*
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.esybrailleapp.network.ApiClient
import com.example.esybrailleapp.network.ApiService
import retrofit2.*

@Composable
fun TranslatorScreen(tts: TextToSpeech, navController: NavHostController) {
    var inputText by remember { mutableStateOf("") }
    var brailleText by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Traductor Braille",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF333333)
        )

        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Español") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val service = ApiClient.instance.create(ApiService::class.java)
                service.translateToBraille(inputText).enqueue(object : Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        brailleText = response.body() ?: "Sin respuesta"
                    }

                    override fun onFailure(call: Call<String>, t: Throwable) {
                        Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            },
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(60.dp)
        ) {
            Text("↻", color = Color.White)
        }

        OutlinedTextField(
            value = brailleText,
            onValueChange = {},
            label = { Text("Braille") },
            modifier = Modifier.fillMaxWidth(),
            enabled = false
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = {
                tts.speak(brailleText, TextToSpeech.QUEUE_FLUSH, null, null)
            }) {
                Icon(Icons.Filled.VolumeUp, contentDescription = "Reproducir", tint = Color(0xFF6200EE))
            }

            IconButton(onClick = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Braille", brailleText)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "Copiado al portapapeles", Toast.LENGTH_SHORT).show()
            }) {
                Icon(Icons.Filled.ContentCopy, contentDescription = "Copiar", tint = Color(0xFF6200EE))
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { navController.navigate("library") }) {
                Text("📚 Biblioteca")
            }
            Button(onClick = { navController.navigate("camera") }) {
                Text("📷 Cámara")
            }
            Button(onClick = { navController.navigate("voice") }) {
                Text("🎙️ Voz")
            }
        }
    }
}

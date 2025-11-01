package com.example.esybrailleapp.ui.screens

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import com.example.esybrailleapp.ui.theme.*
import com.example.esybrailleapp.ui.utils.WindowType
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.example.esybrailleapp.utils.saveBraillePdf
import com.example.esybrailleapp.utils.translateToBraille
import kotlinx.coroutines.delay
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding

private fun createTempImageUri(context: Context): Uri {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_${timeStamp}_"
    val storageDir: File? = context.externalCacheDir
    val imageFile = File.createTempFile(imageFileName, ".jpg", storageDir)
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        imageFile
    )
}

@Composable
fun CameraScreen(navController: NavHostController, windowType: WindowType) {
    val context = LocalContext.current
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }
    var extractedText by remember { mutableStateOf("") }
    var brailleTranslation by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }

    val spanishBorderBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF005f88), BlueAccent)
    )
    val brailleBorderBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF8B0000), Color(0xFFFF6347))
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            tempImageUri?.let { uri ->
                imageBitmap = if (Build.VERSION.SDK_INT < 28) {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source)
                }
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val newTempImageUri = createTempImageUri(context)
            tempImageUri = newTempImageUri
            cameraLauncher.launch(newTempImageUri)
        } else {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(imageBitmap) {
        imageBitmap?.let { bmp ->
            isProcessing = true
            extractedText = ""
            brailleTranslation = ""
            val image = InputImage.fromBitmap(bmp, 0)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    extractedText = visionText.text
                    isProcessing = false
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        context,
                        "Error al reconocer texto: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    extractedText = "Error al reconocer texto."
                    isProcessing = false
                }
        }
    }

    LaunchedEffect(extractedText) {
        delay(300)
        brailleTranslation = if (extractedText.isNotBlank() && !extractedText.startsWith("Error")) {
            translateToBraille(extractedText)
        } else {
            ""
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.6f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (imageBitmap != null) {
                        Image(
                            bitmap = imageBitmap!!.asImageBitmap(),
                            contentDescription = "Foto capturada",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(
                            text = "Usa el botón para abrir la cámara",
                            fontSize = 18.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            Column(
                modifier = Modifier.weight(0.4f)
            ) {
                if (isProcessing) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    // Caja para texto extraído
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, spanishBorderBrush, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBackground)
                    ) {
                        Text(
                            text = extractedText.ifEmpty { "Texto reconocido..." },
                            modifier = Modifier.padding(16.dp)
                                .heightIn(min = 40.dp), // Altura mínima
                            color = if (extractedText.isEmpty()) TextSecondary else TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    // Caja para traducción a Braille
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, brailleBorderBrush, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBackground)
                    ) {
                        Text(
                            text = brailleTranslation.ifEmpty { "Traducción a Braille..." },
                            modifier = Modifier.padding(16.dp)
                                .heightIn(min = 40.dp), // Altura mínima
                            color = if (brailleTranslation.isEmpty()) TextSecondary else TextPrimary
                        )
                    }
                }
            }
        }


        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(0.4.dp))
            Button(
                onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BlueAccent),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Abrir Cámara", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    if (brailleTranslation.isNotBlank()) {
                        val savedFile = saveBraillePdf(context, extractedText, brailleTranslation)
                        if (savedFile != null) {
                            Toast.makeText(context, "Guardado en el historial", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(context, "Error al guardar", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "No hay texto que guardar", Toast.LENGTH_SHORT)
                            .show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenAccent),
                shape = RoundedCornerShape(12.dp),
                enabled = brailleTranslation.isNotBlank()
            ) {
                Text("Guardar en Historial", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
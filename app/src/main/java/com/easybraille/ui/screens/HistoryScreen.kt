package com.easybraille.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.easybraille.ui.theme.BlueAccent
import com.easybraille.ui.theme.CardBackground
import com.easybraille.ui.theme.TextPrimary
import com.easybraille.ui.theme.TextSecondary
import com.easybraille.ui.utils.AuthManager
import com.easybraille.ui.utils.FavoritesManager
import com.easybraille.ui.utils.deletePdf
import com.easybraille.ui.utils.getSavedPdfs
import com.easybraille.ui.utils.openPdf
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavHostController) {
    val context = LocalContext.current

    // Estado para la lista de archivos
    var pdfFiles by remember { mutableStateOf(emptyList<File>()) }

    // Estado "gatillo" para forzar la recarga inmediata
    var refreshTrigger by remember { mutableStateOf(0) }

    var showDialog by remember { mutableStateOf(false) }
    var fileToDelete by remember { mutableStateOf<File?>(null) }

    // Colores personalizados
    val deleteButtonColor = Color(0xFFFF8A80)
    val cancelButtonColor = BlueAccent
    val goldColor = Color(0xFFFFD700)

    // Función para recargar y ordenar la lista
    // Esta función se llamará cada vez que 'refreshTrigger' cambie
    fun refreshFiles() {
        val allFiles = getSavedPdfs(context)
        // Ordenamos: Primero Favoritos, luego por fecha
        pdfFiles = allFiles.sortedWith(
            compareByDescending<File> { FavoritesManager.isFavorite(context, it.name) }
                .thenByDescending { it.lastModified() }
        )
    }

    // Carga inicial y recarga cuando refreshTrigger cambia
    LaunchedEffect(refreshTrigger) {
        if (!AuthManager.isLoggedIn(context)) {
            navController.navigate("login") { popUpTo(0) { inclusive = true } }
            return@LaunchedEffect
        }
        refreshFiles()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Traducciones", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = TextPrimary,
                    navigationIconContentColor = TextPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->

        // --- DIÁLOGO BORRAR ---
        if (showDialog && fileToDelete != null) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                containerColor = CardBackground,
                title = { Text("Confirmar Borrado", color = TextPrimary, fontWeight = FontWeight.Bold) },
                text = { Text("¿Quieres eliminar '${fileToDelete?.name}'?", color = TextSecondary) },
                confirmButton = {
                    Button(
                        onClick = {
                            fileToDelete?.let { file ->
                                if (deletePdf(file)) {
                                    // Al borrar, incrementamos el gatillo para recargar
                                    refreshTrigger++
                                }
                            }
                            showDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = deleteButtonColor, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Eliminar") }
                },
                dismissButton = {
                    Button(
                        onClick = { showDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = cancelButtonColor, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Cancelar") }
                }
            )
        }

        if (pdfFiles.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("No hay traducciones guardadas.", color = TextSecondary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(pdfFiles, key = { it.absolutePath }) { file ->
                    // Pasamos el estado 'refreshTrigger' como key al HistoryItem para asegurar que se repinte si cambia
                    val isFav = FavoritesManager.isFavorite(context, file.name)

                    HistoryItem(
                        file = file,
                        isFavorite = isFav,
                        onFavoriteClick = {
                            FavoritesManager.toggleFavorite(context, file.name)
                            // ¡ESTA ES LA CLAVE! Incrementamos el gatillo para forzar la recarga inmediata
                            refreshTrigger++
                        },
                        onClick = { openPdf(context, file) },
                        onDeleteClick = {
                            fileToDelete = file
                            showDialog = true
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryItem(
    file: File,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val goldColor = Color(0xFFFFD700)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = if (isFavorite) androidx.compose.foundation.BorderStroke(1.dp, goldColor.copy(alpha = 0.6f)) else null
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.PictureAsPdf,
                contentDescription = "PDF",
                tint = if (isFavorite) goldColor else TextSecondary,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = file.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Creado: ${file.lastModified().toFormattedDate()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            // Botón Favorito
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = "Favorito",
                    tint = if (isFavorite) goldColor else TextSecondary
                )
            }

            // Botón Eliminar
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = TextSecondary)
            }
        }
    }
}

private fun Long.toFormattedDate(): String {
    val date = Date(this)
    val format = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return format.format(date)
}
package com.example.esybrailleapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.esybrailleapp.ui.theme.TextPrimary
import com.example.esybrailleapp.ui.theme.TextSecondary
import com.example.esybrailleapp.utils.deletePdf
import com.example.esybrailleapp.utils.getSavedPdfs
import com.example.esybrailleapp.utils.openPdf
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavHostController) {
    val context = LocalContext.current
    var pdfFiles by remember { mutableStateOf(emptyList<File>()) }

    var showDialog by remember { mutableStateOf(false) }
    var fileToDelete by remember { mutableStateOf<File?>(null) }


    fun refreshFiles() {
        pdfFiles = getSavedPdfs(context)
    }

    LaunchedEffect(Unit) {
        refreshFiles()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Traducciones") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->


        if (showDialog && fileToDelete != null) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Confirmar Borrado") },
                text = { Text("¿Estás seguro de que quieres eliminar '${fileToDelete?.name}'?") },
                confirmButton = {
                    Button(
                        onClick = {
                            fileToDelete?.let { file ->
                                if (deletePdf(file)) {
                                    refreshFiles()
                                }
                            }
                            showDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        if (pdfFiles.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay traducciones guardadas.", color = TextSecondary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(pdfFiles, key = { it.absolutePath }) { file ->
                    HistoryItem(
                        file = file,
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
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.PictureAsPdf, contentDescription = "PDF Icon", tint = TextSecondary)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = file.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Creado: ${file.lastModified().toFormattedDate()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

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
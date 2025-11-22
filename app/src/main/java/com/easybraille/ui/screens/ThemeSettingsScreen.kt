package com.easybraille.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.easybraille.ui.theme.TextPrimary
import com.easybraille.ui.theme.TextSecondary
import com.easybraille.utils.ThemeManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSettingsScreen(navController: NavController) {

    val selectedTheme = ThemeManager.currentTheme.collectAsState()

    // Usamos Scaffold para manejar automáticamente el espacio de la barra de estado
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Tema de la aplicación",
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = TextPrimary
                        )
                    }
                },
                // Barra transparente para mantener el estilo minimalista
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        // El color de fondo se adapta automáticamente (Blanco/Negro) gracias a tu AppTheme
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // 👈 ESTO ES CLAVE: Baja el contenido para que no choque
                .padding(24.dp)
        ) {

            ThemeOption(
                title = "Claro",
                selected = selectedTheme.value == 0,
                onClick = { ThemeManager.setTheme(0) }
            )

            ThemeOption(
                title = "Oscuro",
                selected = selectedTheme.value == 1,
                onClick = { ThemeManager.setTheme(1) }
            )

            // He añadido esta opción por si quieres que siga el tema del celular
            ThemeOption(
                title = "Predeterminado del sistema",
                selected = selectedTheme.value == 2,
                onClick = { ThemeManager.setTheme(2) }
            )
        }
    }
}

@Composable
fun ThemeOption(title: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable { onClick() }
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary, // Usa tu BlueAccent
                unselectedColor = TextSecondary
            )
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = TextPrimary, // Se adapta a blanco o negro según el tema
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}
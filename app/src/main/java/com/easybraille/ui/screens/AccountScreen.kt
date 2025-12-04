package com.easybraille.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.easybraille.R
import com.easybraille.ui.ROOT_ROUTE
import com.easybraille.ui.theme.*
import com.easybraille.ui.utils.AuthManager
import com.easybraille.ui.utils.FavoritesManager
import com.easybraille.ui.utils.getSavedPdfs
import com.easybraille.ui.utils.openPdf
import com.easybraille.utils.ThemeManager
import java.io.File
import kotlin.math.sin
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(navController: NavHostController) {

    val context = LocalContext.current
    val userName = AuthManager.getUserName(context)
    val userEmail = AuthManager.getUserEmail(context)
    val isDark = ThemeManager.isDarkTheme()

    var favoriteFiles by remember { mutableStateOf(emptyList<File>()) }

    LaunchedEffect(Unit) {
        val allFiles = getSavedPdfs(context)
        favoriteFiles = allFiles.filter { FavoritesManager.isFavorite(context, it.name) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {

        BrailleDotBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // --- TARJETA DE PERFIL ---
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CardBackground.copy(alpha = 0.95f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp, horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(100.dp)
                            .border(2.dp, BlueAccent.copy(alpha = 0.3f), CircleShape)
                            .padding(4.dp)
                            .clip(CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = if (isDark)
                                R.drawable.easybrailleblanco
                            else
                                R.drawable.easybraillenegro),
                            contentDescription = "Foto de Perfil",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White.copy(alpha = 0.1f))
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = userName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = userEmail,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Documentos Destacados ⭐",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 12.dp)
            )

            if (favoriteFiles.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(CardBackground.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aún no tienes favoritos",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(favoriteFiles) { file ->
                        FavoriteItemCard(file = file, onClick = { openPdf(context, file) })
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- BOTÓN CERRAR SESIÓN ---
            Button(
                onClick = {
                    AuthManager.setLoggedIn(context, false)
                    navController.navigate(ROOT_ROUTE) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = CardBackground.copy(alpha = 0.8f),
                    contentColor = Color(0xFFFF6B6B)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp) // Altura fija y correcta
                // ELIMINADO EL PADDING INTERNO QUE CAUSABA EL ERROR
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Cerrar Sesión",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }

            // --- ESPACIADOR FINAL ---
            // Este Spacer empuja el contenido hacia arriba, separándolo de la barra de gestos
            Spacer(modifier = Modifier.height(32.dp))
        }

        // --- BARRA SUPERIOR ---
        TopAppBar(
            modifier = Modifier.align(Alignment.TopCenter),
            title = { },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = TextPrimary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )
    }
}

@Composable
fun FavoriteItemCard(file: File, onClick: () -> Unit) {
    val goldColor = Color(0xFFFFD700)

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        border = androidx.compose.foundation.BorderStroke(1.dp, goldColor.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.PictureAsPdf,
                contentDescription = null,
                tint = goldColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = file.name,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = goldColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

private data class Dot(val x: Float, val y: Float, val alpha: Float)

@Composable
private fun BrailleDotBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "dots_animation")

    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dots_progress"
    )

    val dots = remember {
        List(40) {
            Dot(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                alpha = Random.nextFloat() * 0.5f + 0.1f
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val dotRadius = 6f

        dots.forEach { dot ->
            drawCircle(
                color = BlueAccent,
                radius = dotRadius,
                center = Offset(
                    x = dot.x * size.width,
                    y = dot.y * size.height
                ),
                alpha = dot.alpha * (0.5f + 0.5f * sin(progress * 2 * Math.PI.toFloat()))
            )
        }
    }
}
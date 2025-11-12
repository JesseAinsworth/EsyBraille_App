package com.example.esybrailleapp.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.esybrailleapp.R
import com.example.esybrailleapp.ROOT_ROUTE
import com.example.esybrailleapp.ui.theme.*
import com.example.esybrailleapp.utils.AuthManager
import kotlin.random.Random
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        BrailleDotBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CardBackground.copy(alpha = 0.8f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.easybrailleblanco),
                        contentDescription = "Foto de Perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Isaac",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Isaac123@gamil.com",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            TextButton(
                onClick = {
                    AuthManager.setLoggedIn(navController.context, false)
                    navController.navigate(ROOT_ROUTE) {
                        popUpTo(0)
                    }
                }
            ) {
                Icon(
                    Icons.Outlined.ExitToApp,
                    contentDescription = "Salir",
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión", color = TextSecondary)
            }
        }


        TopAppBar(
            modifier = Modifier.align(Alignment.TopCenter),
            title = { },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = TextPrimary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )
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
        ), label = "dots_progress"
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
                alpha = dot.alpha * (0.5f + 0.5f * kotlin.math.sin(progress * 2 * Math.PI.toFloat()))
            )
        }
    }
}
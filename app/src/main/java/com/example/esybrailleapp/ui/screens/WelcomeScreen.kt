package com.example.esybrailleapp.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.esybrailleapp.R
import com.example.esybrailleapp.ui.theme.BlueAccent
import com.example.esybrailleapp.ui.utils.WindowType
import kotlinx.coroutines.delay
import kotlin.random.Random


private data class CloudState(
    val scale: Float,
    val alpha: Float,
    val xOffset: Float,
    val yOffset: Float
)

@Composable
fun WelcomeScreen(navController: NavHostController, windowType: WindowType) {
    val logoScale = remember { Animatable(0.5f) }


    LaunchedEffect(Unit) {
        logoScale.animateTo(1f, animationSpec = tween(1000, easing = EaseOutBack))

    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        repeat(7) { index ->
            NeonCloudEffect(seed = index)
        }



        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.easybrailleblanco),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(160.dp)
                    .scale(logoScale.value),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text("Traduciendo Ideas", style = MaterialTheme.typography.headlineMedium, color = Color.White)
            Spacer(modifier = Modifier.height(48.dp))
            Button(
                onClick = { navController.navigate("login") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = BlueAccent)
            ) {
                Text("Iniciar sesión", color = Color.White)
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = { navController.navigate("register") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = BlueAccent),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(BlueAccent))
            ) {
                Text("Registrarse")
            }
        }
    }
}

@Composable
private fun NeonCloudEffect(seed: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "cloud_transition_$seed")


    val initialState by produceState(
        initialValue = CloudState(0f, 0f, 0f, 0f),
        key1 = seed
    ) {
        value = CloudState(
            scale = Random.nextFloat() * 1.0f + 0.8f,
            alpha = Random.nextFloat() * 0.4f + 0.3f,
            xOffset = Random.nextFloat() * 2f - 1f,
            yOffset = Random.nextFloat() * 2f - 1f
        )
    }

    val targetState by produceState(
        initialValue = CloudState(0f, 0f, 0f, 0f),
        key1 = seed
    ) {
        value = CloudState(
            scale = Random.nextFloat() * 1.2f + 1.0f,
            alpha = Random.nextFloat() * 0.2f,
            xOffset = Random.nextFloat() * 2f - 1f,
            yOffset = Random.nextFloat() * 2f - 1f
        )
    }

    val scale by infiniteTransition.animateFloat(
        initialValue = initialState.scale,
        targetValue = targetState.scale,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = Random.nextInt(4000, 8000)),
            repeatMode = RepeatMode.Reverse
        ), label = "cloud_scale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = initialState.alpha,
        targetValue = targetState.alpha,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = Random.nextInt(3000, 7000)),
            repeatMode = RepeatMode.Reverse
        ), label = "cloud_alpha"
    )
    val xOffset by infiniteTransition.animateFloat(
        initialValue = initialState.xOffset,
        targetValue = targetState.xOffset,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = Random.nextInt(5000, 10000)),
            repeatMode = RepeatMode.Reverse
        ), label = "cloud_x"
    )
    val yOffset by infiniteTransition.animateFloat(
        initialValue = initialState.yOffset,
        targetValue = targetState.yOffset,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = Random.nextInt(5000, 10000)),
            repeatMode = RepeatMode.Reverse
        ), label = "cloud_y"
    )

    Canvas(modifier = Modifier
        .fillMaxSize()
        .alpha(alpha)
        .scale(scale)
    ) {
        translate(
            left = size.width / 2 * xOffset,
            top = size.height / 2 * yOffset
        ) {

            val radius = size.minDimension / 2.0f

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        BlueAccent.copy(alpha = 0.7f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = radius
                ),
                radius = radius
            )
        }
    }
}
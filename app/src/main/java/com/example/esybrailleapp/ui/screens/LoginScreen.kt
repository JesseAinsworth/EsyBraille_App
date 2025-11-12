package com.example.esybrailleapp.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.esybrailleapp.R
import com.example.esybrailleapp.ROOT_ROUTE
import com.example.esybrailleapp.network.ApiClient
import com.example.esybrailleapp.network.ApiService
import com.example.esybrailleapp.ui.theme.BlueAccent
import com.example.esybrailleapp.ui.theme.TextPrimary
import com.example.esybrailleapp.ui.theme.TextSecondary
import com.example.esybrailleapp.ui.utils.WindowType
import com.example.esybrailleapp.utils.AuthManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun LoginScreen(navController: NavHostController, windowType: WindowType) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    val indicatorBrush = Brush.horizontalGradient(
        colors = listOf(Color(0xFF005f88), BlueAccent)
    )

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
            modifier = Modifier.size(90.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Bienvenido",
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary
        )
        Text(
            "Inicia sesión para continuar",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(48.dp))

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

            var isEmailFocused by remember { mutableStateOf(false) }
            TextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState -> isEmailFocused = focusState.isFocused }
                    .drawBehind {
                        val strokeWidth = 1.dp.toPx()
                        val y = size.height - strokeWidth / 2
                        drawLine(
                            brush = if (isEmailFocused) indicatorBrush else SolidColor(
                                TextSecondary.copy(alpha = 0.5f)
                            ),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = strokeWidth
                        )
                    },
                label = { Text("Correo electrónico") },
                leadingIcon = { Icon(Icons.Default.Email, "Correo") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = BlueAccent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )

            var isPasswordFocused by remember { mutableStateOf(false) }
            TextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState -> isPasswordFocused = focusState.isFocused }
                    .drawBehind {
                        val strokeWidth = 1.dp.toPx()
                        val y = size.height - strokeWidth / 2
                        drawLine(
                            brush = if (isPasswordFocused) indicatorBrush else SolidColor(
                                TextSecondary.copy(alpha = 0.5f)
                            ),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = strokeWidth
                        )
                    },
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, "Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = BlueAccent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                // 1. Validación básica
                if (email.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "Por favor, ingresa correo y contraseña", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                // 2. Credenciales Locales (para Isaac)
                val correctEmail = "Isaac123@gamil.com"
                val correctPassword = "1234"

                // Comprobamos primero si es el usuario Isaac
                if (email.trim().equals(correctEmail, ignoreCase = true) && password == correctPassword) {

                    AuthManager.saveUserData(context, "Isaac", email)
                    AuthManager.setLoggedIn(context, true)

                    Toast.makeText(context, "Bienvenido", Toast.LENGTH_SHORT).show()
                    navController.navigate("home") {
                        popUpTo(ROOT_ROUTE) { inclusive = true }
                    }
                } else {
                    // 3. Si no es Isaac, intentamos con la API (Base de Datos)
                    val service = ApiClient.instance.create(ApiService::class.java)

                    service.login(email, password).enqueue(object : Callback<String> {
                        override fun onResponse(call: Call<String>, response: Response<String>) {
                            if (response.isSuccessful) {

                                // Si la API devuelve el nombre, úsalo aquí. Si no, ponemos "Usuario"
                                AuthManager.saveUserData(context, "Usuario", email)
                                AuthManager.setLoggedIn(context, true)

                                Toast.makeText(context, "Bienvenido", Toast.LENGTH_SHORT).show()
                                navController.navigate("home") {
                                    popUpTo(ROOT_ROUTE) { inclusive = true }
                                }
                            } else {
                                Toast.makeText(context, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<String>, t: Throwable) {
                            Toast.makeText(context, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Iniciar sesión")
        }

        TextButton(onClick = { navController.navigate("register") }) {
            Text("¿No tienes cuenta? Regístrate")
        }
    }
}
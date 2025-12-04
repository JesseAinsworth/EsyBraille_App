package com.easybraille.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.easybraille.R
import com.easybraille.network.ApiClient
import com.easybraille.network.LoginRequest
import com.easybraille.ui.ROOT_ROUTE
import com.easybraille.ui.theme.BlueAccent
import com.easybraille.ui.theme.TextPrimary
import com.easybraille.ui.theme.TextSecondary
import com.easybraille.ui.utils.AuthManager
import com.easybraille.ui.utils.WindowType
import com.easybraille.network.AuthResponse
import com.easybraille.utils.ThemeManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun LoginScreen(
    navController: NavHostController,
    windowType: WindowType
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Variable para controlar la visibilidad
    var passwordVisible by remember { mutableStateOf(false) }

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
            painter = painterResource(id = if (ThemeManager.isDarkTheme())
                R.drawable.easybrailleblanco
            else
                R.drawable.easybraillenegro),
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
                    .onFocusChanged { isEmailFocused = it.isFocused }
                    .drawBehind {
                        val stroke = 1.dp.toPx()
                        val y = size.height - stroke / 2

                        drawLine(
                            brush = if (isEmailFocused) indicatorBrush
                            else SolidColor(TextSecondary.copy(alpha = 0.5f)),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = stroke
                        )
                    },
                label = { Text("Correo electrónico") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = BlueAccent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            // PASSWORD FIELD
            var isPasswordFocused by remember { mutableStateOf(false) }

            TextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { isPasswordFocused = it.isFocused }
                    .drawBehind {
                        val stroke = 1.dp.toPx()
                        val y = size.height - stroke / 2

                        drawLine(
                            brush = if (isPasswordFocused) indicatorBrush
                            else SolidColor(TextSecondary.copy(alpha = 0.5f)),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = stroke
                        )
                    },
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },


                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),

                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Filled.Visibility
                    else
                        Icons.Filled.VisibilityOff

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = if (passwordVisible) "Ocultar contraseña" else "Ver contraseña")
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = BlueAccent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {

                if (email.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "Completa los campos", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val request = LoginRequest(email.trim(), password)

                ApiClient.instance.login(request)
                    .enqueue(object : Callback<AuthResponse> {

                        override fun onResponse(
                            call: Call<AuthResponse>,
                            response: Response<AuthResponse>
                        ) {
                            if (!response.isSuccessful || response.body() == null) {
                                Toast.makeText(context, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                                return
                            }

                            val auth = response.body()!!
                            val user = auth.user

                            Toast.makeText(context, "Bienvenido ${user.name}", Toast.LENGTH_SHORT).show()

                            AuthManager.saveUserData(
                                context = context,
                                id = user.userId,
                                name = user.name,
                                email = user.email
                            )

                            AuthManager.setLoggedIn(context, true)

                            navController.navigate("home") {
                                popUpTo(ROOT_ROUTE) { inclusive = true }
                            }
                        }

                        override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                            Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Iniciar sesión")
        }

        TextButton(onClick = { navController.navigate("register") }) {
            Text("¿No tienes cuenta? Regístrate")
        }
    }
}
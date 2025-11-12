package com.example.esybrailleapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import com.example.esybrailleapp.network.ApiClient
import com.example.esybrailleapp.network.ApiService
import com.example.esybrailleapp.network.RegisterRequest // 👈 Importa tu nuevo modelo
import com.example.esybrailleapp.ui.theme.BlueAccent
import com.example.esybrailleapp.ui.theme.TextPrimary
import com.example.esybrailleapp.ui.theme.TextSecondary
import com.example.esybrailleapp.ui.utils.WindowType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun RegisterScreen(navController: NavHostController, windowType: WindowType) {
    var name by remember { mutableStateOf("") }
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
            "Crea tu Cuenta",
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary
        )
        Text(
            "Únete a la comunidad",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(48.dp))

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

            var isNameFocused by remember { mutableStateOf(false) }
            TextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { isNameFocused = it.isFocused }
                    .drawBehind {
                        val strokeWidth = 1.dp.toPx()
                        val y = size.height - strokeWidth / 2
                        drawLine(
                            brush = if (isNameFocused) indicatorBrush else SolidColor(TextSecondary.copy(alpha = 0.5f)),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = strokeWidth
                        )
                    },
                label = { Text("Nombre") },
                leadingIcon = { Icon(Icons.Default.Person, "Nombre") },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = BlueAccent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            var isEmailFocused by remember { mutableStateOf(false) }
            TextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { isEmailFocused = it.isFocused }
                    .drawBehind {
                        val strokeWidth = 1.dp.toPx()
                        val y = size.height - strokeWidth / 2
                        drawLine(
                            brush = if (isEmailFocused) indicatorBrush else SolidColor(TextSecondary.copy(alpha = 0.5f)),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = strokeWidth
                        )
                    },
                label = { Text("Correo electrónico") },
                leadingIcon = { Icon(Icons.Default.Email, "Correo") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = BlueAccent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            var isPasswordFocused by remember { mutableStateOf(false) }
            TextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { isPasswordFocused = it.isFocused }
                    .drawBehind {
                        val strokeWidth = 1.dp.toPx()
                        val y = size.height - strokeWidth / 2
                        drawLine(
                            brush = if (isPasswordFocused) indicatorBrush else SolidColor(TextSecondary.copy(alpha = 0.5f)),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = strokeWidth
                        )
                    },
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, "Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
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
                // 1. VALIDACIÓN: Campos vacíos
                if (name.isBlank() || email.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                // 2. LLAMADA AL SERVIDOR
                val service = ApiClient.instance.create(ApiService::class.java)

                // Creamos el objeto con los datos
                val registerRequest = RegisterRequest(
                    name = name,
                    email = email,
                    password = password
                )

                service.register(registerRequest).enqueue(object : Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        if (response.isSuccessful) {
                            Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                            navController.navigate("login")
                        } else {
                            Toast.makeText(context, "Error en el registro: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<String>, t: Throwable) {
                        Toast.makeText(context, "Fallo de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Registrarse")
        }
    }
}
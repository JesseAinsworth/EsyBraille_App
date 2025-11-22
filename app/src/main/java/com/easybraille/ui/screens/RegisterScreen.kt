package comesybraille.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import com.easybraille.network.AuthResponse
import com.easybraille.network.RegisterRequest
import com.easybraille.ui.theme.BlueAccent
import com.easybraille.ui.theme.TextPrimary
import com.easybraille.ui.theme.TextSecondary
import com.easybraille.ui.utils.WindowType
import com.easybraille.utils.ThemeManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun RegisterScreen(navController: NavHostController, windowType: WindowType) {

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // 👇 NUEVO ESTADO: Controla si la contraseña es visible
    var passwordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Brocha del indicador
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

            // --- Nombre
            var isNameFocused by remember { mutableStateOf(false) }
            TextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { isNameFocused = it.isFocused }
                    .drawBehind {
                        val stroke = 1.dp.toPx()
                        val y = size.height - stroke / 2
                        drawLine(
                            brush = if (isNameFocused) indicatorBrush else SolidColor(
                                TextSecondary.copy(alpha = 0.5f)
                            ),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = stroke
                        )
                    },
                label = { Text("Nombre") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Nombre") },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = BlueAccent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            // --- Email
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
                            brush = if (isEmailFocused) indicatorBrush else SolidColor(
                                TextSecondary.copy(alpha = 0.5f)
                            ),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = stroke
                        )
                    },
                label = { Text("Correo electrónico") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Correo") },
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

            // --- Password
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
                            brush = if (isPasswordFocused) indicatorBrush else SolidColor(
                                TextSecondary.copy(alpha = 0.5f)
                            ),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = stroke
                        )
                    },
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Contraseña") },

                // 👇 CAMBIO 1: Alternar entre mostrar texto o puntos
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),

                // 👇 CAMBIO 2: Agregar el icono del ojo al final
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Filled.Visibility
                    else
                        Icons.Filled.VisibilityOff

                    // Un IconButton para que sea clicable
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = if (passwordVisible) "Ocultar contraseña" else "Ver contraseña")
                    }
                },

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

        // --- BOTÓN DE REGISTRO CON VALIDACIÓN ---
        Button(
            onClick = {
                // 1. Validar campos vacíos
                if (name.isBlank() || email.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                // 2. Validar formato de Email (Solo Gmail o Outlook)
                val emailPattern = "^[A-Za-z0-9+_.-]+@(gmail\\.com|outlook\\.com)$"
                if (!email.matches(Regex(emailPattern))) {
                    Toast.makeText(context, "Solo se permiten correos @gmail.com o @outlook.com", Toast.LENGTH_LONG).show()
                    return@Button
                }

                // 3. Validar Contraseña ( >5 caracteres, al menos 1 Mayúscula, al menos 1 Número)
                val passwordPattern = "^(?=.*[A-Z])(?=.*\\d).{6,}$"

                if (!password.matches(Regex(passwordPattern))) {
                    Toast.makeText(context, "La contraseña debe tener más de 5 caracteres, incluir una mayúscula y un número.", Toast.LENGTH_LONG).show()
                    return@Button
                }

                // 4. Si todo es válido, proceder con el registro
                val api = ApiClient.instance
                val registerRequest = RegisterRequest(name, email, password)

                api.register(registerRequest).enqueue(object : Callback<AuthResponse> {
                    override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                        if (response.isSuccessful) {
                            Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                            navController.navigate("login")
                        } else {
                            Toast.makeText(context, "Error en el registro: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                        Toast.makeText(context, "Fallo de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Registrarse")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { navController.navigate("login") }) {
            Text("Ya tengo cuenta")
        }
    }
}
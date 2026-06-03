package com.example.proyecto_aplicaciones_moviles.presentation.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// 1. Nueva importación del ViewModel nativo de Jetpack Compose
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyecto_aplicaciones_moviles.ui.components.WorkConnectButton
import com.example.proyecto_aplicaciones_moviles.ui.components.WorkConnectTextField

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    // 2. Usamos viewModel() en lugar de hiltViewModel()
    viewModel: AuthViewModel = viewModel()
) {
    // Estados locales para los campos de texto
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Observamos el estado del ViewModel (Carga, Éxitos, Errores)
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // LOGO Y TÍTULO
        Text(
            text = "WorkConnect",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A365D),
            modifier = Modifier.padding(top = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // TEXTOS DE BIENVENIDA
        Text(
            text = "Bienvenido de nuevo",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )

        Text(
            text = "Por favor, ingrese su correo electrónico y contraseña.",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 24.dp),
            textAlign = TextAlign.Start
        )

        // CAMPO DE CORREO
        Text(
            text = "Correo Electrónico",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )
        Spacer(modifier = Modifier.height(4.dp))
        WorkConnectTextField(
            value = email,
            onValueChange = {
                email = it
                viewModel.clearError() // Limpiamos el error si el usuario empieza a escribir
            },
            label = "ej. alex.morgan@empresa.com"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // CAMPO DE CONTRASEÑA
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Contraseña",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "¿Olvidó su contraseña?",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1A365D),
                modifier = Modifier.clickable { }
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        WorkConnectTextField(
            value = password,
            onValueChange = {
                password = it
                viewModel.clearError()
            },
            label = "••••••••",
            isPassword = !passwordVisible,
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = "Mostrar contraseña")
                }
            }
        )

        // SECCIÓN DE MENSAJE DE ERROR
        if (state.errorMessage != null) {
            Text(
                text = state.errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                textAlign = TextAlign.Start
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // BOTÓN PRINCIPAL CON ESTADO DE CARGA
        if (state.isLoading) {
            CircularProgressIndicator(color = Color(0xFF1A365D))
        } else {
            WorkConnectButton(
                text = "Iniciar Sesión",
                onClick = {
                    // Llamamos a la lógica del ViewModel
                    viewModel.loginUser(email, password) {
                        onLoginSuccess()
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // DIVISOR "O CONTINUAR CON"
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
            Text(
                text = "O CONTINUAR CON",
                fontSize = 10.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // BOTÓN ÚNICO DE GOOGLE
        OutlinedButton(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent
            ),
            border = BorderStroke(1.dp, Color.LightGray)
        ) {
            Text(
                text = "Continuar con Google",
                color = Color.Black,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // TEXTO INFERIOR PARA REGISTRARSE
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "¿No tiene una cuenta? ", color = Color.Gray)
            Text(
                text = "Crear Cuenta",
                color = Color(0xFF1A365D),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onNavigateToRegister() }
            )
        }
    }
}
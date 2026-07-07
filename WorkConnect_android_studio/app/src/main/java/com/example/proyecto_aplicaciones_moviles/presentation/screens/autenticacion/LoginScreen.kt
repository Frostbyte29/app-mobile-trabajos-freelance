package com.example.proyecto_aplicaciones_moviles.presentation.screens.autenticacion

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyecto_aplicaciones_moviles.core.utils.SessionManager

// 1. IMPORTACIÓN NUEVA: Traemos tu fábrica de dependencias
import com.example.proyecto_aplicaciones_moviles.di.AppContainer

import com.example.proyecto_aplicaciones_moviles.presentation.components.WorkConnectButton
import com.example.proyecto_aplicaciones_moviles.presentation.components.WorkConnectTextField
import com.example.proyecto_aplicaciones_moviles.presentation.screens.auth.AutenticacionViewModel

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: AutenticacionViewModel = viewModel(factory = AppContainer.SharedViewModelFactory)
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "WorkConnect",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A365D),
            modifier = Modifier.padding(top = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

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
                viewModel.clearError()
            },
            label = "ej. alex.morgan@empresa.com"
        )

        Spacer(modifier = Modifier.height(16.dp))

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

        if (state.isLoading) {
            CircularProgressIndicator(color = Color(0xFF1A365D))
        } else {
            WorkConnectButton(
                text = "Iniciar Sesión",
                onClick = {
                    viewModel.iniciarSesion(email, password) {
                        SessionManager.login(email)
                        onLoginSuccess()
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = {
                SessionManager.logout()
                onLoginSuccess()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Continuar como invitado",
                color = Color.Gray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
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

        Spacer(modifier = Modifier.height(32.dp))

    }
}
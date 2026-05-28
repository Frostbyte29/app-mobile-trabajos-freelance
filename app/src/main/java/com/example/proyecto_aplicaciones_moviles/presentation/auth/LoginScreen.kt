package com.example.proyecto_aplicaciones_moviles.presentation.auth

import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto_aplicaciones_moviles.ui.components.WorkConnectButton
import com.example.proyecto_aplicaciones_moviles.ui.components.WorkConnectTextField


@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    // Estados para guardar lo que el usuario escribe
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()), // Permite hacer scroll si el teclado tapa la pantalla
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // 1. LOGO Y TÍTULO DE LA APP
        // Nota: Asegúrate de tener un logo en res/drawable llamado 'ic_workconnect_logo'
        // Si no lo tienes aún, puedes comentar el Image temporalmente.
        /* Image(
            painter = painterResource(id = R.drawable.ic_workconnect_logo),
            contentDescription = "Logo WorkConnect",
            modifier = Modifier.size(80.dp)
        )
        */

        Text(
            text = "WorkConnect",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A365D), // Azul oscuro
            modifier = Modifier.padding(top = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 2. TEXTOS DE BIENVENIDA
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

        // 3. CAMPO DE CORREO
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
            onValueChange = { email = it },
            label = "ej. alex.morgan@empresa.com"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 4. CAMPO DE CONTRASEÑA Y TEXTO DE "OLVIDÓ"
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
                modifier = Modifier.clickable { /* Acción de recuperar contraseña */ }
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        WorkConnectTextField(
            value = password,
            onValueChange = { password = it },
            label = "••••••••",
            isPassword = !passwordVisible,
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = "Mostrar contraseña")
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 5. BOTÓN PRINCIPAL
        WorkConnectButton(
            text = "Iniciar Sesión",
            onClick = { onLoginSuccess() }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 6. BOTONES DE REDES SOCIALES
        SocialLoginButton(text = "Continuar con Google")
        Spacer(modifier = Modifier.height(12.dp))

        Spacer(modifier = Modifier.height(32.dp))

        // 7. TEXTO INFERIOR PARA REGISTRARSE
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

// Componente local para los botones Outline (con borde)
@Composable
fun SocialLoginButton(
    text: String,
    onClick: () -> Unit = {}
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        // Aquí puedes agregar el ícono de la red social correspondiente luego
        Text(
            text = text,
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
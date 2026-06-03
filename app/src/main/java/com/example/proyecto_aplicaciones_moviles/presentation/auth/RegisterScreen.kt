package com.example.proyecto_aplicaciones_moviles.presentation.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PersonAddAlt1
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// 1. Importación del ViewModel nativo
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyecto_aplicaciones_moviles.ui.components.WorkConnectButton
import com.example.proyecto_aplicaciones_moviles.ui.components.WorkConnectTextField

@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onRegisterSuccess: () -> Unit,
    // 2. Inyectamos el ViewModel
    viewModel: AuthViewModel = viewModel()
) {
    // Estados de los campos de texto
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Estado para saber qué tarjeta eligió (1 = Trabajar, 2 = Contratar)
    var selectedRole by remember { mutableStateOf(1) }

    // Estado del Checkbox de términos y condiciones
    var termsAccepted by remember { mutableStateOf(false) }

    // 3. Observamos el estado (cargando, errores)
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // BARRA SUPERIOR
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onNavigateBack() }) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Volver", tint = Color(0xFF1A365D))
            }
            Text(
                text = "WorkConnect",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A365D)
            )
            IconButton(onClick = { }) {
                Icon(imageVector = Icons.Filled.Language, contentDescription = "Idioma", tint = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // TÍTULOS
        Text(text = "Crea tu cuenta", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Únete a la mayor red de talentos y clientes globales.",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        // TARJETAS DE SELECCIÓN DE ROL
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RoleCard(
                modifier = Modifier.weight(1f),
                title = "Quiero trabajar",
                description = "Soy un freelancer buscando proyectos.",
                icon = Icons.Filled.WorkOutline,
                isSelected = selectedRole == 1,
                onClick = { selectedRole = 1 }
            )
            RoleCard(
                modifier = Modifier.weight(1f),
                title = "Quiero contratar",
                description = "Busco contratar al mejor talento.",
                icon = Icons.Filled.PersonAddAlt1,
                isSelected = selectedRole == 2,
                onClick = { selectedRole = 2 }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // CAMPOS DE FORMULARIO
        Text(text = "Nombre Completo", fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(4.dp))
        WorkConnectTextField(
            value = fullName,
            onValueChange = {
                fullName = it
                viewModel.clearError()
            },
            label = "Ej. Alex Morgan"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Correo Electrónico", fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(4.dp))
        WorkConnectTextField(
            value = email,
            onValueChange = {
                email = it
                viewModel.clearError()
            },
            label = "nombre@ejemplo.com"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Contraseña", fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(4.dp))
        WorkConnectTextField(
            value = password,
            onValueChange = {
                password = it
                viewModel.clearError()
            },
            label = "Mínimo 8 caracteres",
            isPassword = !passwordVisible,
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = "Mostrar contraseña")
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // TÉRMINOS Y CONDICIONES
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = termsAccepted,
                onCheckedChange = {
                    termsAccepted = it
                    viewModel.clearError()
                },
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF1A365D))
            )
            Text(text = "Acepto los ", fontSize = 12.sp, color = Color.Gray)
            Text(text = "Términos de Servicio", fontSize = 12.sp, color = Color(0xFF1A365D), modifier = Modifier.clickable { })
        }

        // 4. SECCIÓN DE MENSAJE DE ERROR
        if (state.errorMessage != null) {
            Text(
                text = state.errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp),
                textAlign = TextAlign.Center
            )
        } else {
            Spacer(modifier = Modifier.height(24.dp))
        }

        // 5. BOTÓN CREAR CUENTA CON INDICADOR DE CARGA
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF1A365D))
            }
        } else {
            WorkConnectButton(
                text = "Crear Cuenta ->",
                onClick = {
                    viewModel.registerUser(
                        fullName = fullName,
                        email = email,
                        password = password,
                        termsAccepted = termsAccepted,
                        onNavigateToLogin = { onRegisterSuccess() }
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

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

        // TEXTO INFERIOR PARA VOLVER AL LOGIN
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "¿Ya tienes una cuenta? ", color = Color.Gray)
            Text(
                text = "Iniciar sesión",
                color = Color(0xFF1A365D),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onNavigateBack() }
            )
        }
    }
}

// COMPONENTE LOCAL PARA LAS TARJETAS DE ROL
@Composable
fun RoleCard(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) Color(0xFF1A365D) else Color.LightGray
    val backgroundColor = if (isSelected) Color(0xFFF0F4FF) else Color.Transparent

    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, borderColor),
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = Color(0xFF1A365D), modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = description, fontSize = 12.sp, color = Color.Gray, lineHeight = 16.sp)
        }
    }
}
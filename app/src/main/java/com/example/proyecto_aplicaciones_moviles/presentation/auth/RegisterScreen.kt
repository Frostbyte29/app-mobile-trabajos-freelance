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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// Importaciones de tus componentes reutilizables
import com.example.proyecto_aplicaciones_moviles.ui.components.WorkConnectButton
import com.example.proyecto_aplicaciones_moviles.ui.components.WorkConnectTextField

@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onRegisterSuccess: () -> Unit
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA)) // Fondo sutil
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // 1. BARRA SUPERIOR
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

        // 2. TÍTULOS
        Text(text = "Crea tu cuenta", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Únete a la mayor red de talentos y clientes globales.",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 3. TARJETAS DE SELECCIÓN DE ROL
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

        // 4. CAMPOS DE FORMULARIO
        Text(text = "Nombre Completo", fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(4.dp))
        WorkConnectTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = "Ej. Alex Morgan"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Correo Electrónico", fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(4.dp))
        WorkConnectTextField(
            value = email,
            onValueChange = { email = it },
            label = "nombre@ejemplo.com"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Contraseña", fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(4.dp))
        WorkConnectTextField(
            value = password,
            onValueChange = { password = it },
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

        // 5. TÉRMINOS Y CONDICIONES
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = termsAccepted,
                onCheckedChange = { termsAccepted = it },
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF1A365D))
            )
            Text(text = "Acepto los ", fontSize = 12.sp, color = Color.Gray)
            Text(text = "Términos de Servicio", fontSize = 12.sp, color = Color(0xFF1A365D), modifier = Modifier.clickable { })
            Text(text = " y la ", fontSize = 12.sp, color = Color.Gray)
            Text(text = "Política", fontSize = 12.sp, color = Color(0xFF1A365D), modifier = Modifier.clickable { })
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 6. BOTÓN CREAR CUENTA
        WorkConnectButton(
            text = "Crear Cuenta ->",
            onClick = {
                if (termsAccepted) {
                    onRegisterSuccess()
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 7. DIVISOR "O CONTINUAR CON"
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

        // 8. BOTÓN ÚNICO DE GOOGLE
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
            // Nota: Descomenta el bloque de Image cuando agregues tu ícono a res/drawable
            /*
            Image(
                painter = painterResource(id = R.drawable.ic_google),
                contentDescription = "Logo Google",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            */
            Text(
                text = "Continuar con Google",
                color = Color.Black,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 9. TEXTO INFERIOR PARA VOLVER AL LOGIN
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

// --- COMPONENTE LOCAL PARA LAS TARJETAS DE ROL ---
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
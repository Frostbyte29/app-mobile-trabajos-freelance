package com.example.proyecto_aplicaciones_moviles.presentation.screens.publish

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto_aplicaciones_moviles.presentation.screens.main.SharedProjectViewModel
import com.example.proyecto_aplicaciones_moviles.presentation.components.WorkConnectButton
import com.example.proyecto_aplicaciones_moviles.presentation.components.WorkConnectTextField
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PublishScreen(
    viewModel: SharedProjectViewModel // Recibe el ViewModel compartido
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }

    // Estados para simular la carga y el éxito
    var isLoading by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Publicar Proyecto", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A365D))
        Text(text = "Encuentra al talento perfecto para tu idea.", fontSize = 14.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(32.dp))

        if (showSuccess) {
            // Mensaje de éxito
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFD1FAE5) // Verde claro
            ) {
                Text(
                    text = "¡Proyecto publicado con éxito! Revisa la pestaña de Inicio.",
                    color = Color(0xFF065F46),
                    modifier = Modifier.padding(16.dp),
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            WorkConnectButton(
                text = "Publicar otro proyecto",
                onClick = {
                    showSuccess = false
                    title = ""; description = ""; budget = ""; category = ""
                }
            )
        } else {
            // FORMULARIO
            Text(text = "Título del Proyecto", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            WorkConnectTextField(value = title, onValueChange = { title = it }, label = "Ej. Diseño de App Móvil")

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Categoría o Habilidad Principal", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            WorkConnectTextField(value = category, onValueChange = { category = it }, label = "Ej. Diseño UI/UX")

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Presupuesto (S/.)", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            WorkConnectTextField(value = budget, onValueChange = { budget = it }, label = "Ej. 1500")

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Descripción", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            // TextField más grande para la descripción
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                placeholder = { Text("Describe los detalles de tu proyecto...", color = Color.Gray) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White, focusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // BOTÓN DE PUBLICAR
            if (isLoading) {
                CircularProgressIndicator(color = Color(0xFF1A365D))
            } else {
                WorkConnectButton(
                    text = "Publicar Proyecto",
                    onClick = {
                        if (title.isNotBlank() && budget.isNotBlank() && description.isNotBlank()) {
                            coroutineScope.launch {
                                isLoading = true
                                delay(1500) // Simular carga
                                viewModel.addProject(title, budget, category, description)
                                isLoading = false
                                showSuccess = true
                            }
                        }
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(80.dp))
    }
}
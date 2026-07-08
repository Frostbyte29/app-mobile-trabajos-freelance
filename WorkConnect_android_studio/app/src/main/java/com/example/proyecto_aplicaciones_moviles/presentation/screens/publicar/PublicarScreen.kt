package com.example.proyecto_aplicaciones_moviles.presentation.screens.publicar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto_aplicaciones_moviles.core.utils.SessionManager
import com.example.proyecto_aplicaciones_moviles.presentation.components.WorkConnectButton
import com.example.proyecto_aplicaciones_moviles.presentation.components.WorkConnectTextField
import com.example.proyecto_aplicaciones_moviles.presentation.main.SharedProjectViewModel
import kotlinx.coroutines.launch
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicarScreen(
    viewModel: SharedProjectViewModel
) {
    val rolActivo = SessionManager.activeRole

    if (SessionManager.isGuest) {
        ContenidoBloqueadoParaPublicar()
        return
    }

    val tipoOferta = if (rolActivo == "reclutador") "trabajo" else "servicio"

    val perfilViewModel: com.example.proyecto_aplicaciones_moviles.presentation.screens.perfil.PerfilViewModel =
        androidx.lifecycle.viewmodel.compose.viewModel(factory = com.example.proyecto_aplicaciones_moviles.di.AppContainer.SharedViewModelFactory)
    val profileState by perfilViewModel.state.collectAsState()

    val nombreVisible: String = when (rolActivo) {
        "reclutador" -> {
            profileState.empresaNombre.takeIf { it.isNotBlank() }
                ?: SessionManager.currentEmpresaNombre?.takeIf { it.isNotBlank() }
                ?: "${profileState.nombres} ${profileState.apellidos}".trim().takeIf { it.isNotBlank() }
                ?: SessionManager.currentNombreCompleto?.takeIf { it.isNotBlank() }
                ?: SessionManager.currentUserEmail?.substringBefore("@")
                ?: ""
        }
        else -> {
            val fromState = "${profileState.nombres} ${profileState.apellidos}".trim()
            fromState.takeIf { it.isNotBlank() }
                ?: SessionManager.currentNombreCompleto?.takeIf { it.isNotBlank() }
                ?: SessionManager.currentUserEmail?.substringBefore("@")
                ?: ""
        }
    }

    val userId = SessionManager.currentUserId ?: ""

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val categorias by viewModel.categorias.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(16.dp))

        val tituloPantalla = if (tipoOferta == "trabajo") "Publicar Oferta de Trabajo" else "Ofrecer mis Servicios"
        val subtitulo = if (tipoOferta == "trabajo")
            "Encuentra al talento perfecto para tu proyecto."
        else
            "Muestra lo que sabes hacer a empresas y reclutadores."

        Text(tituloPantalla, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A365D))
        Text(subtitulo, fontSize = 14.sp, color = Color.Gray)

        Spacer(Modifier.height(16.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFF0F4FF)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (tipoOferta == "trabajo") Icons.Filled.Business else Icons.Filled.Person,
                    contentDescription = null,
                    tint = Color(0xFF1A365D),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Se publicará como:",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = nombreVisible.ifBlank { "Sin nombre configurado" },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A365D)
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        if (showSuccess) {
            Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = Color(0xFFD1FAE5)) {
                Text(
                    "¡Publicado con éxito! Revisa la pestaña de Inicio.",
                    color = Color(0xFF065F46),
                    modifier = Modifier.padding(16.dp),
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(Modifier.height(24.dp))
            WorkConnectButton(
                text = "Publicar otro",
                onClick = {
                    showSuccess = false
                    title = ""; description = ""; budget = ""; selectedCategory = ""
                }
            )
        } else {

            val labelTitulo = if (tipoOferta == "trabajo") "Título del trabajo" else "Título del servicio"
            val hintTitulo = if (tipoOferta == "trabajo") "Ej. Desarrollador Android Senior" else "Ej. Diseño de logos profesionales"
            Text(labelTitulo, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(4.dp))
            WorkConnectTextField(value = title, onValueChange = { title = it; errorMessage = null }, label = hintTitulo)

            Spacer(Modifier.height(16.dp))

            Text("Categoría", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(4.dp))
            ExposedDropdownMenuBox(
                expanded = dropdownExpanded && categorias.isNotEmpty(),
                onExpandedChange = { dropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Selecciona una categoría", color = Color.Gray, fontSize = 14.sp) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White
                    ),
                    singleLine = true
                )
                ExposedDropdownMenu(
                    expanded = dropdownExpanded && categorias.isNotEmpty(),
                    onDismissRequest = { dropdownExpanded = false }
                ) {
                    categorias.forEach { categoria ->
                        DropdownMenuItem(
                            text = { Text(categoria) },
                            onClick = {
                                selectedCategory = categoria
                                dropdownExpanded = false
                                errorMessage = null
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            val labelBudget = if (tipoOferta == "trabajo") "Sueldo ofrecido (S/.)" else "Sueldo Esperado (S/.)"
            val hintBudget = if (tipoOferta == "trabajo") "Ej. 1500" else "Ej. 200"
            Text(labelBudget, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(4.dp))
            WorkConnectTextField(
                value = budget,
                onValueChange = { nuevoValor ->
                    if (nuevoValor.isEmpty() || nuevoValor.all { it.isDigit() }) {
                        budget = nuevoValor
                        errorMessage = null
                    }
                },
                label = hintBudget,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(Modifier.height(16.dp))

            val labelDesc = if (tipoOferta == "trabajo") "Descripción del trabajo" else "Descripción de tu servicio"
            val hintDesc = if (tipoOferta == "trabajo")
                "Describe los requisitos y responsabilidades..."
            else
                "Describe qué incluye tu servicio, experiencia, plazos..."
            Text(labelDesc, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(4.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it; errorMessage = null },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                placeholder = { Text(hintDesc, color = Color.Gray) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )

            if (errorMessage != null) {
                Text(
                    errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
            }

            Spacer(Modifier.height(32.dp))

            val labelBoton = if (tipoOferta == "trabajo") "Publicar Oferta de Trabajo" else "Publicar mi Servicio"

            if (isLoading) {
                Box(Modifier.fillMaxWidth(), Alignment.Center) { CircularProgressIndicator(color = Color(0xFF1A365D)) }
            } else {
                WorkConnectButton(
                    text = labelBoton,
                    onClick = {
                        when {
                            title.isBlank() -> errorMessage = "El título es obligatorio."
                            selectedCategory.isBlank() -> errorMessage = "Selecciona una categoría."
                            budget.isBlank() -> errorMessage = "El presupuesto/precio es obligatorio."
                            budget.toDoubleOrNull() == null -> errorMessage = "Debe ser un número válido."
                            description.isBlank() -> errorMessage = "La descripción es obligatoria."
                            else -> {
                                coroutineScope.launch {
                                    isLoading = true
                                    val exito = viewModel.agregarProyecto(
                                        title = title,
                                        budget = budget,
                                        category = selectedCategory,
                                        description = description,
                                        empresa = nombreVisible,
                                        tipoOferta = tipoOferta,
                                        creadoPorId = userId.ifBlank { null }
                                    )
                                    isLoading = false
                                    if (exito) showSuccess = true
                                    else errorMessage = "Error al publicar. Verifica tu conexión."
                                }
                            }
                        }
                    }
                )
            }
        }

        Spacer(Modifier.height(80.dp))
    }
}

@Composable
private fun ContenidoBloqueadoParaPublicar() {
    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(Icons.Filled.Lock, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
            Spacer(Modifier.height(16.dp))
            Text(
                "Inicia sesión para publicar.",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A365D),
                textAlign = TextAlign.Center
            )
        }
    }
}
package com.example.proyecto_aplicaciones_moviles.presentation.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyecto_aplicaciones_moviles.core.utils.SessionManager
import com.example.proyecto_aplicaciones_moviles.presentation.components.GuestPromptDialog
import com.example.proyecto_aplicaciones_moviles.presentation.main.SharedProjectViewModel
import com.example.proyecto_aplicaciones_moviles.presentation.screens.activity.MyActivityViewModel
@Composable
fun HomeScreen(
    viewModel: SharedProjectViewModel,
    activityViewModel: MyActivityViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToDetail: (String) -> Unit = {},
    onNavigateToNotificaciones: () -> Unit = {}
) {
    val projects by viewModel.projects.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val categorias by viewModel.categorias.collectAsState()
    val filtroActivo by viewModel.filtroCategoria.collectAsState()
    val activityState by activityViewModel.state.collectAsState()

    // Refrescamos el filtro cuando HomeScreen se vuelve visible
    // y también si el rol activo cambia (usuario cambia perfil en Perfil)
    val rolActivo = SessionManager.activeRole
    LaunchedEffect(rolActivo) {
        viewModel.refrescarFiltro()
    }

    var showGuestDialog by remember { mutableStateOf(false) }
    // Proyecto seleccionado — abre el diálogo de confirmación de postulación
    var proyectoParaPostular by remember { mutableStateOf<com.example.proyecto_aplicaciones_moviles.domain.model.Project?>(null) }
    var mensajePostulacion by remember { mutableStateOf("") }

    // Limpia el banner de éxito tras 3 segundos
    LaunchedEffect(activityState.postulacionExitosa) {
        if (activityState.postulacionExitosa) {
            kotlinx.coroutines.delay(3000)
            activityViewModel.clearPostulacionExitosa()
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (SessionManager.isGuest) showGuestDialog = true
                    // El FAB se puede conectar a la tab de Publicar en el futuro
                },
                containerColor = Color(0xFF1A365D),
                contentColor = Color.White,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Add, "Agregar")
            }
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->

        // Box necesario para poder superponer el banner de éxito sobre la lista
        Box(modifier = Modifier.fillMaxSize()) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(16.dp)) }

                // 1. BARRA SUPERIOR
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.LightGray),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.Person, "Avatar", tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "WorkConnect",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A365D)
                            )
                        }
                        IconButton(onClick = { onNavigateToNotificaciones() }) {
                            Icon(Icons.Filled.NotificationsNone, "Notificaciones")
                        }
                    }
                }

                // 2. BUSCADOR
                item {
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Buscar proyectos, habilidades o clientes...", color = Color.Gray, fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Filled.Search, "Buscar", tint = Color.Gray) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            unfocusedBorderColor = Color.LightGray
                        ),
                        singleLine = true
                    )
                }

                // 3. CHIPS DE FILTRO — funcionales con categorías de AWS
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FilterChipCustom(
                                text = "Todos",
                                isSelected = filtroActivo == "Todos",
                                onClick = { viewModel.establecerFiltroCategoria("Todos") }
                            )
                        }
                        items(categorias) { categoria ->
                            FilterChipCustom(
                                text = categoria,
                                isSelected = filtroActivo == categoria,
                                onClick = { viewModel.establecerFiltroCategoria(categoria) }
                            )
                        }
                    }
                }

                // 4. TÍTULO DE SECCIÓN — adaptado al rol activo
                item {
                    val tituloSeccion = when (rolActivo) {
                        "reclutador" -> "Freelancers disponibles"
                        "candidato"  -> "Ofertas de trabajo"
                        else         -> "Publicaciones recientes"
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(tituloSeccion, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Ver todo", fontSize = 14.sp, color = Color(0xFF1A365D), fontWeight = FontWeight.Medium)
                    }
                }

                // 5. LISTA DE PROYECTOS DE AWS
                if (isLoading) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(32.dp), Alignment.Center) {
                            CircularProgressIndicator(color = Color(0xFF1A365D))
                        }
                    }
                } else if (projects.isEmpty()) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(32.dp), Alignment.Center) {
                            Text("Aún no hay proyectos publicados.", fontSize = 14.sp, color = Color.Gray)
                        }
                    }
                } else {
                    items(projects) { project ->
                        // Proyecto propio del reclutador — no debe tener botón Postularse
                        val esProyectoPropio = !SessionManager.currentUserId.isNullOrBlank() &&
                            project.creadoPorId == SessionManager.currentUserId

                        // Verificar si ya se postuló a este proyecto
                        val yaPostulado = activityState.postulaciones.any { it.vacanteId == project.id }

                        ProjectCard(
                            title = project.title,
                            price = "S/.${project.budget}",
                            priceType = "Precio Fijo",
                            company = project.company,
                            timeAgo = formatearFechaHoraHome(project.createdAt),
                            description = project.description,
                            tags = listOf(project.category),
                            badgeText = when {
                                esProyectoPropio -> "Tu oferta"
                                yaPostulado      -> "Ya postulado"
                                else             -> "Nuevo"
                            },
                            isPrimaryAction = !esProyectoPropio && !yaPostulado,
                            onApplyClick = {
                                when {
                                    SessionManager.isGuest -> showGuestDialog = true
                                    yaPostulado -> { /* ya postulado, no hacer nada */ }
                                    esProyectoPropio -> onNavigateToDetail(project.id)
                                    else -> {
                                        proyectoParaPostular = project
                                        mensajePostulacion = ""
                                    }
                                }
                            },
                            onVerDetalle = { onNavigateToDetail(project.id) }
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }

            // DIÁLOGO de confirmación de postulación
            proyectoParaPostular?.let { proyecto ->
                AlertDialog(
                    onDismissRequest = { proyectoParaPostular = null },
                    title = { Text("Postularse", fontWeight = FontWeight.Bold) },
                    text = {
                        Column {
                            Text("¿Deseas postularte a \"${proyecto.title}\"?", fontSize = 14.sp)
                            Spacer(Modifier.height(12.dp))
                            OutlinedTextField(
                                value = mensajePostulacion,
                                onValueChange = { mensajePostulacion = it },
                                label = { Text("Mensaje de presentación (opcional)") },
                                modifier = Modifier.fillMaxWidth().height(100.dp),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                activityViewModel.postularse(
                                    vacanteId = proyecto.id,
                                    mensajePresentacion = mensajePostulacion
                                )
                                proyectoParaPostular = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A365D))
                        ) { Text("Postularme", color = Color.White) }
                    },
                    dismissButton = {
                        TextButton(onClick = { proyectoParaPostular = null }) {
                            Text("Cancelar", color = Color.Gray)
                        }
                    }
                )
            }

            // BANNER de postulación exitosa (superpuesto abajo)
            if (activityState.postulacionExitosa) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(24.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFD1FAE5)
                ) {
                    Text(
                        "¡Postulación enviada! Revísala en la pestaña Actividad.",
                        color = Color(0xFF065F46),
                        modifier = Modifier.padding(16.dp),
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp
                    )
                }
            }

            // DIÁLOGO de invitado
            GuestPromptDialog(
                showDialog = showGuestDialog,
                onDismiss = { showGuestDialog = false },
                onNavigateToLogin = {
                    showGuestDialog = false
                    onNavigateToLogin()
                }
            )

        } // fin Box
    } // fin Scaffold
}

// ─────────────────────────────────────────────────────────────────────────────
// COMPONENTES REUTILIZABLES
// ─────────────────────────────────────────────────────────────────────────────

// Formatea ISO 8601 → "15 ene 2024 • 14:32" o "Reciente" si está vacío
fun formatearFechaHoraHome(iso: String): String {
    if (iso.isBlank()) return "Reciente"
    return try {
        val limpio = iso.replace("Z", "").replace("T", " ").take(16)
        val partes = limpio.split(" ")
        val fecha = partes[0]
        val hora  = partes.getOrNull(1) ?: ""
        val meses = listOf("","ene","feb","mar","abr","may","jun","jul","ago","sep","oct","nov","dic")
        val (anio, mes, dia) = fecha.split("-")
        val mesNombre = meses.getOrElse(mes.toIntOrNull() ?: 0) { mes }
        if (hora.isNotBlank()) "$dia $mesNombre $anio • $hora" else "$dia $mesNombre $anio"
    } catch (e: Exception) {
        "Reciente"
    }
}

@Composable
fun FilterChipCustom(text: String, isSelected: Boolean, onClick: () -> Unit = {}) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) Color(0xFF294485) else Color(0xFFE9ECEF),
        modifier = Modifier.height(32.dp).clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = text,
                color = if (isSelected) Color.White else Color.Black,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ProjectCard(
    title: String,
    price: String,
    priceType: String,
    company: String,
    timeAgo: String,
    description: String,
    tags: List<String>,
    badgeText: String? = null,
    isPrimaryAction: Boolean,
    onApplyClick: () -> Unit,
    onVerDetalle: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (badgeText != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF6EE7B7).copy(alpha = 0.3f),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = badgeText,
                        color = Color(0xFF047857),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = price, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A365D))
                    Text(text = priceType, fontSize = 12.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            val subText = if (timeAgo.isNotEmpty()) "$company • $timeAgo" else company
            Text(text = subText, fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = description, fontSize = 13.sp, color = Color.DarkGray, lineHeight = 18.sp)
            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(tags.size) { index ->
                    Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFF3F4F6)) {
                        Text(
                            text = tags[index],
                            fontSize = 11.sp,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                if (isPrimaryAction) {
                    // Botón Postularse — ocupa todo el ancho
                    Button(
                        onClick = onApplyClick,
                        modifier = Modifier.weight(1f).height(40.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF294485))
                    ) {
                        Text("Postularse", fontSize = 14.sp)
                    }
                    Spacer(Modifier.width(8.dp))
                    // Botón "Ver" como secundario para ir al detalle
                    OutlinedButton(
                        onClick = onVerDetalle,
                        modifier = Modifier.height(40.dp),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Text("Ver", color = Color(0xFF294485), fontSize = 13.sp)
                    }
                } else {
                    // Proyecto propio o reclutador viendo servicio — botón "Ver detalle" completo
                    OutlinedButton(
                        onClick = onVerDetalle,
                        modifier = Modifier.fillMaxWidth().height(40.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFF294485))
                    ) {
                        Text("Ver detalle", color = Color(0xFF294485), fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

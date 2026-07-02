package com.example.proyecto_aplicaciones_moviles.presentation.screens.activity

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto_aplicaciones_moviles.core.utils.SessionManager
import com.example.proyecto_aplicaciones_moviles.domain.model.Postulacion
import com.example.proyecto_aplicaciones_moviles.domain.model.Project

@Composable
fun MyActivityScreen(
    viewModel: MyActivityViewModel,
    onNavigateToDetail: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    // Recarga al entrar a la pantalla para reflejar nuevas ofertas o postulaciones
    LaunchedEffect(Unit) {
        viewModel.cargarActividad()
    }

    // Auto-limpiar mensajes de éxito
    LaunchedEffect(state.postulacionExitosa) {
        if (state.postulacionExitosa) { kotlinx.coroutines.delay(3000); viewModel.clearPostulacionExitosa() }
    }
    LaunchedEffect(state.estadoActualizadoExito) {
        if (state.estadoActualizadoExito) { kotlinx.coroutines.delay(3000); viewModel.clearEstadoActualizado() }
    }

    if (SessionManager.isGuest) { GuestActivityContent(); return }

    // Si hay una oferta expandida, mostramos la pantalla de postulantes
    if (state.ofertaExpandidaId != null) {
        PostulantesScreen(state = state, viewModel = viewModel, onVerDetalle = onNavigateToDetail)
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // TÍTULO
        Text(
            text = "Mi Actividad",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A365D),
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)
        )

        // CARGANDO
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF1A365D))
            }
            return@Column
        }

        // ERROR
        if (state.errorMessage != null && state.roles.isEmpty()) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                    Text(state.errorMessage!!, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.cargarActividad() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A365D)),
                        shape = RoundedCornerShape(8.dp)
                    ) { Text("Reintentar", color = Color.White) }
                }
            }
            return@Column
        }

        // TABS — solo si tiene los dos roles
        val tieneDosRoles = state.roles.contains("candidato") && state.roles.contains("reclutador")

        if (tieneDosRoles) {
            TabRow(
                selectedTabIndex = if (state.tabActivo == "candidato") 0 else 1,
                containerColor = Color.White,
                contentColor = Color(0xFF1A365D)
            ) {
                Tab(
                    selected = state.tabActivo == "candidato",
                    onClick = { viewModel.setTabActivo("candidato") },
                    text = {
                        Text(
                            "Mis Postulaciones",
                            fontWeight = if (state.tabActivo == "candidato") FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = state.tabActivo == "reclutador",
                    onClick = { viewModel.setTabActivo("reclutador") },
                    text = {
                        Text(
                            "Mis Ofertas",
                            fontWeight = if (state.tabActivo == "reclutador") FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        // Banners de éxito
        if (state.postulacionExitosa) {
            SuccessBanner("¡Postulación enviada con éxito!")
        }

        // CONTENIDO según rol/tab
        val mostrarCandidato = !tieneDosRoles && state.roles.contains("candidato")
        val mostrarReclutador = !tieneDosRoles && state.roles.contains("reclutador")

        when {
            tieneDosRoles && state.tabActivo == "candidato" ->
                PostulacionesContent(state.postulaciones, onNavigateToDetail)

            tieneDosRoles && state.tabActivo == "reclutador" ->
                OfertasContent(state.misProyectos, viewModel, onNavigateToDetail)

            mostrarCandidato ->
                PostulacionesContent(state.postulaciones, onNavigateToDetail)

            mostrarReclutador ->
                OfertasContent(state.misProyectos, viewModel, onNavigateToDetail)

            else ->
                EmptyActivityContent(SessionManager.activeRole)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// PANTALLA: Lista de postulantes de una oferta (vista reclutador)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun PostulantesScreen(
    state: MyActivityState,
    viewModel: MyActivityViewModel,
    onVerDetalle: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // Barra con botón volver + botón ver detalle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.cerrarPostulantes() }) {
                Icon(Icons.Filled.ArrowBack, "Volver", tint = Color(0xFF1A365D))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Postulantes",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A365D)
                )
                Text(
                    text = state.ofertaExpandidaTitulo,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            // Botón para ver el detalle completo de la oferta
            if (state.ofertaExpandidaId != null) {
                TextButton(onClick = { onVerDetalle(state.ofertaExpandidaId) }) {
                    Text("Ver oferta", color = Color(0xFF1A365D), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        HorizontalDivider(color = Color.LightGray)

        if (state.estadoActualizadoExito) {
            SuccessBanner("Estado actualizado con éxito")
        }
        if (state.errorMessage != null) {
            Surface(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFEE2E2)
            ) {
                Text(state.errorMessage, color = Color(0xFF991B1B), modifier = Modifier.padding(16.dp), fontSize = 13.sp)
            }
        }

        if (state.isLoadingPostulaciones) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF1A365D))
            }
            return@Column
        }

        if (state.postulacionesOferta.isEmpty()) {
            EmptySection("Aún nadie se ha postulado a esta oferta.")
            return@Column
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.postulacionesOferta) { postulacion ->
                PostulanteCard(
                    postulacion = postulacion,
                    isActualizando = state.isActualizandoEstado,
                    onCambiarEstado = { nuevoEstado ->
                        viewModel.actualizarEstadoPostulacion(postulacion.id, nuevoEstado)
                    },
                    onVerDetalle = { /* detalle de postulante: pendiente de implementar */ }
                )
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SECCIÓN: Mis postulaciones (candidato)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun PostulacionesContent(
    postulaciones: List<Postulacion>,
    onNavigateToDetail: (String) -> Unit = {}
) {
    if (postulaciones.isEmpty()) {
        EmptySection("Aún no te has postulado a ningún proyecto.\nExplora las ofertas en Inicio.")
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(postulaciones) { postulacion ->
            PostulacionCard(
                postulacion = postulacion,
                onClick = { onNavigateToDetail(postulacion.vacanteId) }
            )
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SECCIÓN: Mis ofertas publicadas (reclutador)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun OfertasContent(
    proyectos: List<Project>,
    viewModel: MyActivityViewModel,
    onNavigateToDetail: (String) -> Unit = {}
) {
    if (proyectos.isEmpty()) {
        EmptySection("Aún no has publicado ninguna oferta.\nVe a Publicar para crear tu primera oferta.")
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(proyectos) { proyecto ->
            OfertaCard(
                proyecto = proyecto,
                onClick = { viewModel.verPostulantesDeOferta(proyecto.id, proyecto.title) },
                onVerDetalle = { onNavigateToDetail(proyecto.id) }
            )
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Utilidad: formatea ISO 8601 → "15 ene 2024 • 14:32"
// ─────────────────────────────────────────────────────────────────────────────
private fun formatearFechaHora(iso: String): String {
    return try {
        // Formato: "2024-01-15T14:32:00.000Z"
        val limpio = iso.replace("Z", "").replace("T", " ").take(16) // "2024-01-15 14:32"
        val partes = limpio.split(" ")
        val fecha = partes[0] // "2024-01-15"
        val hora  = if (partes.size > 1) partes[1] else ""
        val meses = listOf("","ene","feb","mar","abr","may","jun","jul","ago","sep","oct","nov","dic")
        val (anio, mes, dia) = fecha.split("-")
        val mesNombre = meses.getOrElse(mes.toIntOrNull() ?: 0) { mes }
        if (hora.isNotBlank()) "$dia $mesNombre $anio • $hora" else "$dia $mesNombre $anio"
    } catch (e: Exception) {
        iso.take(16).replace("T", " ")
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// COMPONENTE: Tarjeta de postulación (vista candidato)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun PostulacionCard(postulacion: Postulacion, onClick: () -> Unit) {
    val (badgeColor, textColor) = when (postulacion.estado) {
        "aceptado"    -> Color(0xFF6EE7B7).copy(alpha = 0.3f) to Color(0xFF047857)
        "rechazado"   -> Color(0xFFFECACA).copy(alpha = 0.5f) to Color(0xFF991B1B)
        "en_revision" -> Color(0xFFFEF3C7).copy(alpha = 0.6f) to Color(0xFF92400E)
        else          -> Color(0xFFE0E7FF).copy(alpha = 0.6f) to Color(0xFF3730A3)
    }
    val estadoTexto = when (postulacion.estado) {
        "postulado"   -> "Postulado"
        "en_revision" -> "En revisión"
        "aceptado"    -> "Aceptado"
        "rechazado"   -> "Rechazado"
        else          -> postulacion.estado
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text(
                    text = postulacion.tituloVacante.ifBlank { postulacion.vacanteId.take(12) },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
                Surface(shape = RoundedCornerShape(8.dp), color = badgeColor) {
                    Text(estadoTexto, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
            if (postulacion.mensajePresentacion.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(postulacion.mensajePresentacion, fontSize = 13.sp, color = Color.Gray,
                    maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 18.sp)
            }
            Spacer(Modifier.height(8.dp))
            Text("Fecha: ${formatearFechaHora(postulacion.fechaPostulacion)}", fontSize = 12.sp, color = Color.LightGray)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// COMPONENTE: Tarjeta de oferta (reclutador) — clickeable para ver postulantes
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun OfertaCard(
    proyecto: Project,
    onClick: () -> Unit,
    onVerDetalle: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(proyecto.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black,
                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(Modifier.height(4.dp))
                    Text(proyecto.category, fontSize = 12.sp, color = Color.Gray)
                    Spacer(Modifier.height(4.dp))
                    Text(proyecto.description, fontSize = 13.sp, color = Color.DarkGray,
                        maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 18.sp)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFF3F4F6)) {
                            Text("S/.${proyecto.budget}", fontSize = 11.sp, color = Color(0xFF1A365D),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                        Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFF3F4F6)) {
                            Text(proyecto.company, fontSize = 11.sp, color = Color.DarkGray,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }
                }
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Filled.KeyboardArrowRight, "Ver postulantes", tint = Color.LightGray,
                    modifier = Modifier.clickable { onClick() })
            }
            Spacer(Modifier.height(12.dp))
            // Botones de acción
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onVerDetalle,
                    modifier = Modifier.weight(1f).height(36.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFF1A365D)),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text("Ver detalle", color = Color(0xFF1A365D), fontSize = 12.sp)
                }
                Button(
                    onClick = onClick,
                    modifier = Modifier.weight(1f).height(36.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A365D)),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text("Ver postulantes", color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// COMPONENTE: Tarjeta de postulante (vista reclutador en detalle de oferta)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun PostulanteCard(
    postulacion: Postulacion,
    isActualizando: Boolean,
    onCambiarEstado: (String) -> Unit,
    onVerDetalle: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    val (badgeColor, textColor) = when (postulacion.estado) {
        "aceptado"    -> Color(0xFF6EE7B7).copy(alpha = 0.3f) to Color(0xFF047857)
        "rechazado"   -> Color(0xFFFECACA).copy(alpha = 0.5f) to Color(0xFF991B1B)
        "en_revision" -> Color(0xFFFEF3C7).copy(alpha = 0.6f) to Color(0xFF92400E)
        else          -> Color(0xFFE0E7FF).copy(alpha = 0.6f) to Color(0xFF3730A3)
    }
    val estadoTexto = when (postulacion.estado) {
        "postulado"   -> "Postulado"
        "en_revision" -> "En revisión"
        "aceptado"    -> "Aceptado"
        "rechazado"   -> "Rechazado"
        else          -> postulacion.estado
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onVerDetalle),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text(
                    text = postulacion.nombreCandidato.ifBlank { postulacion.candidatoId.take(12) },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
                Surface(shape = RoundedCornerShape(8.dp), color = badgeColor) {
                    Text(estadoTexto, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }

            if (postulacion.mensajePresentacion.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "\"${postulacion.mensajePresentacion}\"",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    lineHeight = 18.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.height(8.dp))
            Text("Fecha: ${formatearFechaHora(postulacion.fechaPostulacion)}", fontSize = 12.sp, color = Color.LightGray)

            Spacer(Modifier.height(12.dp))

            // Botón para cambiar estado (dropdown) - con stopPropagation para evitar click en card
            Box {
                OutlinedButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isActualizando,
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent)
                ) {
                    Text(
                        text = if (isActualizando) "Actualizando..." else "Cambiar estado",
                        color = Color(0xFF1A365D),
                        fontSize = 13.sp
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    listOf(
                        "en_revision" to "Marcar en revisión",
                        "aceptado"    to "Aceptar candidato",
                        "rechazado"   to "Rechazar candidato"
                    ).forEach { (estado, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                menuExpanded = false
                                onCambiarEstado(estado)
                            }
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Componentes auxiliares
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun SuccessBanner(text: String) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFD1FAE5)
    ) {
        Text(text, color = Color(0xFF065F46), modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun EmptySection(mensaje: String) {
    Box(Modifier.fillMaxSize(), Alignment.Center) {
        Text(mensaje, fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center,
            modifier = Modifier.padding(32.dp), lineHeight = 20.sp)
    }
}

@Composable
private fun EmptyActivityContent(activeRole: String?) {
    Box(Modifier.fillMaxSize(), Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Text("📋", fontSize = 48.sp)
            Spacer(Modifier.height(16.dp))
            Text(
                text = if (activeRole == "reclutador") "Aún no has publicado ninguna oferta."
                       else "Aún no tienes actividad registrada.",
                fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Gray, textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun GuestActivityContent() {
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA)), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Text("🔒", fontSize = 48.sp)
            Spacer(Modifier.height(16.dp))
            Text("Inicia sesión para ver tu actividad.", fontSize = 16.sp,
                fontWeight = FontWeight.Bold, color = Color.Gray, textAlign = TextAlign.Center)
        }
    }
}

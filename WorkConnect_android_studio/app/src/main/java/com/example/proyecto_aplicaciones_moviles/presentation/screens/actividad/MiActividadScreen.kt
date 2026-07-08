package com.example.proyecto_aplicaciones_moviles.presentation.screens.actividad

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
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
import com.example.proyecto_aplicaciones_moviles.core.utils.rolToDisplayName
import com.example.proyecto_aplicaciones_moviles.domain.model.Contrato
import com.example.proyecto_aplicaciones_moviles.domain.model.Postulacion
import com.example.proyecto_aplicaciones_moviles.domain.model.Proyecto
import com.example.proyecto_aplicaciones_moviles.domain.model.Valoracion

@Composable
fun MiActividadScreen(
    viewModel: MiActividadViewModel,
    onNavigateToDetail: (String) -> Unit = {},
    onNavigateToChat: (conversationId: String, nombreOtroParticipante: String) -> Unit = { _, _ -> },
    onNavigateToPerfilPublico: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarActividad()
    }

    LaunchedEffect(state.postulacionExitosa) {
        if (state.postulacionExitosa) { kotlinx.coroutines.delay(3000); viewModel.clearPostulacionExitosa() }
    }
    LaunchedEffect(state.estadoActualizadoExito) {
        if (state.estadoActualizadoExito) { kotlinx.coroutines.delay(3000); viewModel.clearEstadoActualizado() }
    }
    LaunchedEffect(state.contratoFinalizadoExito) {
        if (state.contratoFinalizadoExito) { kotlinx.coroutines.delay(3000); viewModel.clearContratoFinalizadoExito() }
    }
    LaunchedEffect(state.valoracionEnviadaExito) {
        if (state.valoracionEnviadaExito) { kotlinx.coroutines.delay(3000); viewModel.clearValoracionExito() }
    }

    // Evento one-shot: navegar al chat cuando el ViewModel resuelve la conversación
    LaunchedEffect(state.chatNavConvId) {
        val convId = state.chatNavConvId ?: return@LaunchedEffect
        val nombre = state.chatNavNombre ?: ""
        viewModel.clearChatNav()
        onNavigateToChat(convId, nombre)
    }

    if (SessionManager.isGuest) { GuestActividadContent(); return }

    if (state.ofertaExpandidaId != null) {
        PostulantesScreen(
            state = state,
            viewModel = viewModel,
            onVerDetalle = onNavigateToDetail,
            onNavigateToChat = onNavigateToChat,
            onNavigateToPerfilPublico = onNavigateToPerfilPublico
        )
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

        val sinDatos = state.postulaciones.isEmpty() && state.misProyectos.isEmpty() &&
                state.misServicios.isEmpty() && state.contratosFreelancer.isEmpty() &&
                state.contratosContratante.isEmpty()
        if (state.isLoading && sinDatos) {
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
                            "Como ${rolToDisplayName("candidato").lowercase()}",
                            fontWeight = if (state.tabActivo == "candidato") FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = state.tabActivo == "reclutador",
                    onClick = { viewModel.setTabActivo("reclutador") },
                    text = {
                        Text(
                            "Como ${rolToDisplayName("reclutador").lowercase()}",
                            fontWeight = if (state.tabActivo == "reclutador") FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        if (state.postulacionExitosa) { SuccessBanner("¡Postulación enviada con éxito!") }
        if (state.contratoFinalizadoExito) { SuccessBanner("¡Contrato marcado como finalizado!") }
        if (state.valoracionEnviadaExito) { SuccessBanner("¡Valoración enviada con éxito!") }

        val mostrarCandidato = !tieneDosRoles && state.roles.contains("candidato")
        val mostrarReclutador = !tieneDosRoles && state.roles.contains("reclutador")

        when {
            tieneDosRoles && state.tabActivo == "candidato" ->
                PostulacionesContent(viewModel, onNavigateToDetail)

            tieneDosRoles && state.tabActivo == "reclutador" ->
                OfertasContent(viewModel, onNavigateToDetail)

            mostrarCandidato ->
                PostulacionesContent(viewModel, onNavigateToDetail)

            mostrarReclutador ->
                OfertasContent(viewModel, onNavigateToDetail)

            else ->
                ContenidoActividadVacia(SessionManager.activeRole)
        }

        if (state.mostrarFormValoracion) {
            ValoracionDialog(state = state, viewModel = viewModel)
        }
    }
}

@Composable
private fun PostulantesScreen(
    state: MiActividadState,
    viewModel: MiActividadViewModel,
    onVerDetalle: (String) -> Unit = {},
    onNavigateToChat: (String, String) -> Unit = { _, _ -> },
    onNavigateToPerfilPublico: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
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
            SeccionVacia("Aún nadie se ha postulado a esta oferta.")
            return@Column
        }

        val aceptados = state.postulacionesOferta.filter { it.estado == "aceptado" }
        val resto = state.postulacionesOferta.filter { it.estado != "aceptado" }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (aceptados.isNotEmpty()) {
                item {
                    Text(
                        "✓ Candidatos aceptados",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF047857),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                items(aceptados) { postulacion ->
                    PostulanteCard(
                        postulacion = postulacion,
                        isActualizando = state.isActualizandoEstado,
                        onCambiarEstado = { nuevoEstado ->
                            viewModel.actualizarEstadoPostulacion(postulacion.id, nuevoEstado)
                        },
                        onVerDetalle = { onNavigateToPerfilPublico(postulacion.candidatoId) },
                        onIniciarChat = {
                            viewModel.iniciarChatConCandidato(
                                candidatoId = postulacion.candidatoId,
                                nombreCandidato = postulacion.nombreCandidato.ifBlank { postulacion.candidatoId }
                            )
                        }
                    )
                }
            }
            if (resto.isNotEmpty()) {
                if (aceptados.isNotEmpty()) {
                    item {
                        Text(
                            "Otras postulaciones",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A365D),
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    }
                }
                items(resto) { postulacion ->
                    PostulanteCard(
                        postulacion = postulacion,
                        isActualizando = state.isActualizandoEstado,
                        onCambiarEstado = { nuevoEstado ->
                            viewModel.actualizarEstadoPostulacion(postulacion.id, nuevoEstado)
                        },
                        onVerDetalle = { onNavigateToPerfilPublico(postulacion.candidatoId) },
                        onIniciarChat = {
                            viewModel.iniciarChatConCandidato(
                                candidatoId = postulacion.candidatoId,
                                nombreCandidato = postulacion.nombreCandidato.ifBlank { postulacion.candidatoId }
                            )
                        }
                    )
                }
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun PostulacionesContent(
    viewModel: MiActividadViewModel,
    onNavigateToDetail: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val filtro = state.filtroContratosFreelancer
    val contratosFiltrados = when (filtro) {
        "en_curso"   -> state.contratosFreelancer.filter { it.estado == "en_curso" }
        "finalizado" -> state.contratosFreelancer.filter { it.estado == "finalizado" }
        else         -> state.contratosFreelancer
    }

    if (state.postulaciones.isEmpty() && state.contratosFreelancer.isEmpty() && state.misServicios.isEmpty()) {
        SeccionVacia("Aún no te has postulado a ningún proyecto.\nExplora las ofertas en Inicio.")
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (state.postulaciones.isNotEmpty()) {
            item {
                Text(
                    "Mis postulaciones",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A365D),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }
        items(state.postulaciones) { postulacion ->
            PostulacionCard(
                postulacion = postulacion,
                onClick = { onNavigateToDetail(postulacion.vacanteId) }
            )
        }

        if (state.misServicios.isNotEmpty()) {
            item {
                Text(
                    "Mis servicios ofrecidos",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A365D),
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            }
            items(state.misServicios) { servicio ->
                ServicioCard(
                    servicio = servicio,
                    onVerDetalle = { onNavigateToDetail(servicio.id) }
                )
            }
        }

        if (state.contratosFreelancer.isNotEmpty()) {
            item {
                Text(
                    "Mis contratos",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A365D),
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
                FiltroContratoChips(
                    filtroActual = filtro,
                    onFiltroChange = { viewModel.setFiltroContratos(esFreelancer = true, filtro = it) }
                )
            }
            if (contratosFiltrados.isEmpty()) {
                item { SeccionVaciaInline("No hay contratos en este estado.") }
            } else {
                items(contratosFiltrados) { contrato ->
                    val receptorId = contrato.contratanteId
                    val yaValorado = state.valoracionesEmitidas.any { v ->
                        v.usuarioReceptorId == receptorId && v.proyectoId == contrato.ofertaId
                    }
                    ContratoCard(
                        contrato = contrato,
                        esContratante = false,
                        isFinalizando = false,
                        yaValorado = yaValorado,
                        onFinalizar = {},
                        onValorar = {
                            viewModel.abrirFormValoracion(
                                contratoId = contrato.id,
                                receptorId = receptorId,
                                tituloContrato = contrato.tituloOferta,
                                ofertaId = contrato.ofertaId
                            )
                        }
                    )
                }
            }
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
private fun OfertasContent(
    viewModel: MiActividadViewModel,
    onNavigateToDetail: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val filtro = state.filtroContratosContratante
    val contratosFiltrados = when (filtro) {
        "en_curso"   -> state.contratosContratante.filter { it.estado == "en_curso" }
        "finalizado" -> state.contratosContratante.filter { it.estado == "finalizado" }
        else         -> state.contratosContratante
    }

    if (state.misProyectos.isEmpty() && state.contratosContratante.isEmpty()) {
        SeccionVacia("Aún no has publicado ninguna oferta.\nVe a Publicar para crear tu primera oferta.")
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(state.misProyectos) { proyecto ->
            val isFinalizada = state.contratosContratante.any {
                it.ofertaId == proyecto.id && it.estado == "finalizado"
            }
            val contratoEnCursoId = state.contratosContratante
                .firstOrNull { it.ofertaId == proyecto.id && it.estado == "en_curso" }?.id
            OfertaCard(
                proyecto = proyecto,
                isFinalizada = isFinalizada,
                contratoEnCursoId = contratoEnCursoId,
                isFinalizando = state.isFinalizandoContrato,
                onClick = { viewModel.verPostulantesDeOferta(proyecto.id, proyecto.title) },
                onVerDetalle = { onNavigateToDetail(proyecto.id) },
                onFinalizar = { contratoId -> viewModel.finalizarContrato(contratoId) }
            )
        }
        if (state.contratosContratante.isNotEmpty()) {
            item {
                Text(
                    "Contratos activos y finalizados",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A365D),
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
                FiltroContratoChips(
                    filtroActual = filtro,
                    onFiltroChange = { viewModel.setFiltroContratos(esFreelancer = false, filtro = it) }
                )
            }
            if (contratosFiltrados.isEmpty()) {
                item { SeccionVaciaInline("No hay contratos en este estado.") }
            } else {
                items(contratosFiltrados) { contrato ->
                    val receptorId = contrato.freelancerId
                    val yaValorado = state.valoracionesEmitidas.any { v ->
                        v.usuarioReceptorId == receptorId && v.proyectoId == contrato.ofertaId
                    }
                    ContratoCard(
                        contrato = contrato,
                        esContratante = true,
                        isFinalizando = state.isFinalizandoContrato,
                        yaValorado = yaValorado,
                        onFinalizar = { viewModel.finalizarContrato(contrato.id) },
                        onValorar = {
                            viewModel.abrirFormValoracion(
                                contratoId = contrato.id,
                                receptorId = receptorId,
                                tituloContrato = contrato.tituloOferta,
                                ofertaId = contrato.ofertaId
                            )
                        }
                    )
                }
            }
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}

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

@Composable
private fun ServicioCard(servicio: Proyecto, onVerDetalle: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text(
                    servicio.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFE0E7FF)) {
                    Text("Servicio", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3730A3),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
            if (servicio.description.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(servicio.description, fontSize = 13.sp, color = Color.Gray,
                    maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 18.sp)
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFF3F4F6)) {
                    Text("S/.${servicio.budget}", fontSize = 11.sp, color = Color(0xFF1A365D),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
                Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFF3F4F6)) {
                    Text(servicio.category, fontSize = 11.sp, color = Color.DarkGray,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = onVerDetalle,
                modifier = Modifier.fillMaxWidth().height(36.dp),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFF1A365D)),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Text("Ver detalle", color = Color(0xFF1A365D), fontSize = 12.sp)
            }
        }
    }
}

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

            // TITULO + BADGE
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = postulacion.tituloVacante.ifBlank { postulacion.vacanteId.take(12) },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = badgeColor
                ) {
                    Text(
                        estadoTexto,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            if (postulacion.mensajePresentacion.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    postulacion.mensajePresentacion,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )
            }

            Spacer(Modifier.height(10.dp))

            Text(
                "Fecha: ${formatearFechaHora(postulacion.fechaPostulacion)}",
                fontSize = 12.sp,
                color = Color.LightGray
            )
        }
    }
}

@Composable
private fun OfertaCard(
    proyecto: Proyecto,
    isFinalizada: Boolean = false,
    contratoEnCursoId: String? = null,
    isFinalizando: Boolean = false,
    onClick: () -> Unit,
    onVerDetalle: () -> Unit = {},
    onFinalizar: (String) -> Unit = {}
) {
    var showConfirmarFinalizar by remember { mutableStateOf(false) }

    if (showConfirmarFinalizar) {
        AlertDialog(
            onDismissRequest = { showConfirmarFinalizar = false },
            title = { Text("¿Finalizar proceso?", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Text(
                    "Al finalizar, ambas partes podrán valorarse y ya no se aceptarán nuevas postulaciones.",
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmarFinalizar = false
                        if (contratoEnCursoId != null) onFinalizar(contratoEnCursoId)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
                ) { Text("Finalizar", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmarFinalizar = false }) { Text("Cancelar") }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(proyecto.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black,
                            maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                        if (isFinalizada) {
                            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFD1FAE5)) {
                                Text("Finalizada", fontSize = 10.sp, fontWeight = FontWeight.Bold,
                                    color = Color(0xFF065F46),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                            }
                        }
                    }
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
            // Fila principal: Ver detalle + Ver postulantes
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
            if (!isFinalizada) {
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { showConfirmarFinalizar = true },
                    modifier = Modifier.fillMaxWidth().height(36.dp),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isFinalizando,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text(
                        if (isFinalizando) "Finalizando..." else "Finalizar proceso",
                        color = Color.White, fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun PostulanteCard(
    postulacion: Postulacion,
    isActualizando: Boolean,
    onCambiarEstado: (String) -> Unit,
    onVerDetalle: () -> Unit,
    onIniciarChat: () -> Unit = {}
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

            if (!postulacion.linkedinUrl.isNullOrBlank() || !postulacion.repoUrl.isNullOrBlank()) {
                Spacer(Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (!postulacion.linkedinUrl.isNullOrBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Filled.Link, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(14.dp))
                            Text(
                                postulacion.linkedinUrl!!,
                                fontSize = 12.sp,
                                color = Color(0xFF64748B),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    if (!postulacion.repoUrl.isNullOrBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Filled.Link, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(14.dp))
                            Text(
                                postulacion.repoUrl!!,
                                fontSize = 12.sp,
                                color = Color(0xFF64748B),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            Text("Fecha: ${formatearFechaHora(postulacion.fechaPostulacion)}", fontSize = 12.sp, color = Color.LightGray)

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
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

                Button(
                    onClick = onIniciarChat,
                    modifier = Modifier.height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A365D)),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Text("Mensaje", color = Color.White, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun ContratoCard(
    contrato: Contrato,
    esContratante: Boolean,
    isFinalizando: Boolean,
    yaValorado: Boolean = false,
    onFinalizar: () -> Unit,
    onValorar: () -> Unit
) {
    var showConfirmarFinalizar by remember { mutableStateOf(false) }
    val esFinalizado = contrato.estado == "finalizado"

    if (showConfirmarFinalizar) {
        AlertDialog(
            onDismissRequest = { showConfirmarFinalizar = false },
            title = { Text("¿Finalizar contrato?", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Text(
                    "Al finalizar este contrato ambas partes podrán valorarse. Ya no se aceptarán nuevas postulaciones para esta oferta.",
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { showConfirmarFinalizar = false; onFinalizar() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
                ) { Text("Aceptar", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmarFinalizar = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    }
    val badgeColor = if (esFinalizado) Color(0xFFD1FAE5) else Color(0xFFE0E7FF)
    val badgeTextColor = if (esFinalizado) Color(0xFF065F46) else Color(0xFF3730A3)
    val estadoTexto = if (esFinalizado) "Finalizado" else "En curso"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text(
                    contrato.tituloOferta,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.width(8.dp))
                Surface(shape = RoundedCornerShape(8.dp), color = badgeColor) {
                    Text(estadoTexto, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = badgeTextColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }

            Spacer(Modifier.height(6.dp))
            Text(
                "Inicio: ${formatearFechaHora(contrato.fechaInicio)}",
                fontSize = 12.sp, color = Color.LightGray
            )
            if (contrato.fechaFin != null) {
                Text(
                    "Fin: ${formatearFechaHora(contrato.fechaFin)}",
                    fontSize = 12.sp, color = Color.LightGray
                )
            }

            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (esContratante && !esFinalizado) {
                    Button(
                        onClick = { showConfirmarFinalizar = true },
                        enabled = !isFinalizando,
                        modifier = Modifier.weight(1f).height(36.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text(
                            if (isFinalizando) "Finalizando..." else "Marcar finalizado",
                            color = Color.White, fontSize = 12.sp
                        )
                    }
                }
                if (esFinalizado) {
                    OutlinedButton(
                        onClick = onValorar,
                        modifier = Modifier.weight(1f).height(36.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFF59E0B)),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text(
                            if (yaValorado) "Editar valoración" else "Valorar",
                            color = Color(0xFFF59E0B),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ValoracionDialog(state: MiActividadState, viewModel: MiActividadViewModel) {
    AlertDialog(
        onDismissRequest = { viewModel.cerrarFormValoracion() },
        title = {
            Text(
                if (state.valoracionIdAEditar != null) "Editar valoración"
                else "Valorar: ${state.tituloContratoAValorar}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Selector de estrellas
                Text("Calificación", fontSize = 13.sp, color = Color(0xFF475569), fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                    (1..5).forEach { estrella ->
                        IconButton(onClick = { viewModel.onPuntuacionChange(estrella) }, modifier = Modifier.size(40.dp)) {
                            Icon(
                                imageVector = if (estrella <= state.puntuacionSeleccionada)
                                    Icons.Filled.Star
                                else Icons.Filled.StarBorder,
                                contentDescription = null,
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = state.comentarioValoracion,
                    onValueChange = viewModel::onComentarioValoracionChange,
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    placeholder = { Text("Comentario (requerido)...", color = Color(0xFFCBD5E1), fontSize = 13.sp) },
                    shape = RoundedCornerShape(8.dp)
                )
                if (state.errorMessage != null) {
                    Text(state.errorMessage, color = Color(0xFF991B1B), fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { viewModel.enviarValoracion() },
                enabled = !state.isEnviandoValoracion,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B))
            ) {
                Text(
                    when {
                        state.isEnviandoValoracion -> "Enviando..."
                        state.valoracionIdAEditar != null -> "Guardar"
                        else -> "Enviar"
                    }
                )
            }
        },
        dismissButton = {
            TextButton(onClick = { viewModel.cerrarFormValoracion() }) {
                Text("Cancelar", color = Color(0xFF64748B))
            }
        }
    )
}

@Composable
private fun FiltroContratoChips(filtroActual: String, onFiltroChange: (String) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    ) {
        listOf(
            "todos"      to "Todos",
            "en_curso"   to "En curso",
            "finalizado" to "Finalizados"
        ).forEach { (key, label) ->
            FilterChip(
                selected = filtroActual == key,
                onClick = { onFiltroChange(key) },
                label = { Text(label, fontSize = 12.sp) }
            )
        }
    }
}

@Composable
private fun SeccionVaciaInline(mensaje: String) {
    Text(
        mensaje,
        fontSize = 13.sp,
        color = Color.Gray,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    )
}

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
private fun SeccionVacia(mensaje: String) {
    Box(Modifier.fillMaxSize(), Alignment.Center) {
        Text(mensaje, fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center,
            modifier = Modifier.padding(32.dp), lineHeight = 20.sp)
    }
}

@Composable
private fun ContenidoActividadVacia(activeRole: String?) {
    Box(Modifier.fillMaxSize(), Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Icon(Icons.Filled.Assignment, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
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
private fun GuestActividadContent() {
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA)), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Icon(Icons.Filled.Lock, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
            Spacer(Modifier.height(16.dp))
            Text(
                "Inicia sesión para tus actividades.",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A365D),
                textAlign = TextAlign.Center
            )
        }
    }
}
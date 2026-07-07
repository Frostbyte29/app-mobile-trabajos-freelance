package com.example.proyecto_aplicaciones_moviles.presentation.screens.perfilpublico

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyecto_aplicaciones_moviles.core.utils.SessionManager
import com.example.proyecto_aplicaciones_moviles.core.utils.rolToDisplayName
import com.example.proyecto_aplicaciones_moviles.di.AppContainer
import com.example.proyecto_aplicaciones_moviles.domain.model.Contrato
import com.example.proyecto_aplicaciones_moviles.domain.model.Valoracion

@Composable
fun PerfilPublicoScreen(
    usuarioId: String,
    onBack: () -> Unit,
    onNavigateToChat: (conversationId: String, nombreOtroParticipante: String) -> Unit = { _, _ -> },
    viewModel: PerfilPublicoViewModel = viewModel(factory = AppContainer.SharedViewModelFactory)
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(usuarioId) {
        viewModel.cargarPerfil(usuarioId)
    }

    LaunchedEffect(state.chatNavConvId) {
        val convId = state.chatNavConvId ?: return@LaunchedEffect
        val nombre = state.chatNavNombre ?: ""
        viewModel.clearChatNav()
        onNavigateToChat(convId, nombre)
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA))) {
        Surface(
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, "Volver", tint = Color(0xFF1A365D))
                }
                Text(
                    "Perfil",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A365D),
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF1A365D))
                }
            }

            state.errorMessage != null && state.usuario == null -> {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(
                            state.errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.cargarPerfil(usuarioId) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A365D)),
                            shape = RoundedCornerShape(8.dp)
                        ) { Text("Reintentar", color = Color.White) }
                    }
                }
            }

            state.usuario != null -> {
                val usuario = state.usuario!!
                val nombreCompleto = "${usuario.nombres} ${usuario.apellidos}".trim()
                    .ifBlank { "Usuario" }
                val roles = usuario.roles
                val esPropioUsuario = usuarioId == SessionManager.currentUserId
                val puedeEnviarMensaje = !SessionManager.isGuest && !esPropioUsuario

                LazyColumn(
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Avatar con inicial
                            Box(
                                modifier = Modifier
                                    .size(88.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF1A365D)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    nombreCompleto.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Spacer(Modifier.height(16.dp))

                            Text(
                                nombreCompleto,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A365D),
                                textAlign = TextAlign.Center
                            )

                            // Roles como chips
                            if (roles.isNotEmpty()) {
                                Spacer(Modifier.height(8.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    roles.forEach { rol ->
                                        Surface(
                                            shape = RoundedCornerShape(20.dp),
                                            color = Color(0xFFE0E7FF)
                                        ) {
                                            Text(
                                                rolToDisplayName(rol),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF3730A3),
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            if (state.valoraciones.isNotEmpty()) {
                                Spacer(Modifier.height(12.dp))
                                val promedio = state.valoraciones.map { it.puntuacion }.average()
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Filled.Star,
                                        null,
                                        tint = Color(0xFFF59E0B),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        String.format("%.1f", promedio),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF1A365D)
                                    )
                                    Text(
                                        " (${state.valoraciones.size} ${if (state.valoraciones.size == 1) "valoración" else "valoraciones"})",
                                        fontSize = 12.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        }
                    }

                    if (usuario.acercaDe.isNotBlank()) {
                        item {
                            Spacer(Modifier.height(12.dp))
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        "Sobre mí",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1A365D)
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        usuario.acercaDe,
                                        fontSize = 14.sp,
                                        color = Color(0xFF475569),
                                        lineHeight = 22.sp
                                    )
                                }
                            }
                        }
                    }

                    if (puedeEnviarMensaje) {
                        item {
                            Spacer(Modifier.height(12.dp))
                            OutlinedButton(
                                onClick = {
                                    if (!state.isBuscandoChat) viewModel.iniciarChat(usuarioId)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .height(52.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    width = 1.5.dp
                                ),
                                enabled = !state.isBuscandoChat
                            ) {
                                if (state.isBuscandoChat) {
                                    CircularProgressIndicator(
                                        color = Color(0xFF1A365D),
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        Icons.Filled.Send,
                                        null,
                                        tint = Color(0xFF1A365D),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "Enviar mensaje",
                                        color = Color(0xFF1A365D),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    if (state.trabajosRealizados.isNotEmpty()) {
                        item {
                            Spacer(Modifier.height(20.dp))
                            Text(
                                "Trabajos realizados",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A365D),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                        items(state.trabajosRealizados) { contrato ->
                            TrabajoRealizadoCard(contrato)
                        }
                    }

                    if (state.valoraciones.isNotEmpty()) {
                        item {
                            Spacer(Modifier.height(20.dp))
                            Text(
                                "Valoraciones",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A365D),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                        items(state.valoraciones) { valoracion ->
                            ValoracionCard(valoracion)
                        }
                    } else if (!state.isLoading) {
                        item {
                            Spacer(Modifier.height(20.dp))
                            Text(
                                "Sin valoraciones todavía.",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// COMPONENTE: Trabajo realizado
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun TrabajoRealizadoCard(contrato: Contrato) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFD1FAE5)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Work, null, tint = Color(0xFF059669), modifier = Modifier.size(20.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    contrato.tituloOferta.ifBlank { "Proyecto" },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A365D)
                )
                if (contrato.fechaFin != null) {
                    Text(
                        "Finalizado: ${contrato.fechaFin.formatFecha()}",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// COMPONENTE: Tarjeta de valoración
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ValoracionCard(valoracion: Valoracion) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1A365D))
                            .border(2.dp, Color(0xFFE0E7FF), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            valoracion.nombreEmisor.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column {
                        Text(
                            valoracion.nombreEmisor.ifBlank { "Usuario anónimo" },
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A365D)
                        )
                        Text(
                            valoracion.fechaCreacion.formatFecha(),
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                Surface(shape = RoundedCornerShape(10.dp), color = Color(0xFFFEF3C7)) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        repeat(valoracion.puntuacion) {
                            Icon(Icons.Filled.Star, null, tint = Color(0xFFF59E0B), modifier = Modifier.size(14.dp))
                        }
                        repeat(5 - valoracion.puntuacion) {
                            Icon(Icons.Filled.StarBorder, null, tint = Color(0xFFF59E0B), modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }

            if (valoracion.comentario.isNotBlank()) {
                Spacer(Modifier.height(10.dp))
                Text(
                    "\"${valoracion.comentario}\"",
                    fontSize = 14.sp,
                    color = Color(0xFF475569),
                    lineHeight = 20.sp
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Utilitaria: ISO 8601 → "15 ene 2024"
// ─────────────────────────────────────────────────────────────────────────────
private fun String.formatFecha(): String {
    if (this.isBlank()) return ""
    return try {
        val fecha = this.replace("Z", "").replace("T", " ").take(10)
        val (anio, mes, dia) = fecha.split("-")
        val meses = listOf("","ene","feb","mar","abr","may","jun","jul","ago","sep","oct","nov","dic")
        val mesNombre = meses.getOrElse(mes.toIntOrNull() ?: 0) { mes }
        "$dia $mesNombre $anio"
    } catch (e: Exception) {
        this.take(10)
    }
}

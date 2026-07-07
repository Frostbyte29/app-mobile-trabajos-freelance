package com.example.proyecto_aplicaciones_moviles.presentation.screens.notificaciones

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.proyecto_aplicaciones_moviles.core.utils.SessionManager
import com.example.proyecto_aplicaciones_moviles.domain.model.Notificacion

@Composable
fun NotificacionScreen(
    viewModel: NotificacionViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        if (!SessionManager.currentUserId.isNullOrBlank()) {
            viewModel.cargarNotificaciones()
        }
    }

    val noLeidas = state.notificaciones.count { !it.leida }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, "Volver", tint = Color(0xFF1A365D))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Notificaciones",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A365D)
                    )
                    if (noLeidas > 0) {
                        Text(
                            "$noLeidas sin leer",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
                if (noLeidas > 0) {
                    TextButton(onClick = { viewModel.marcarTodasLeidas() }) {
                        Text("Leer todas", fontSize = 12.sp, color = Color(0xFF1A365D))
                    }
                }
                IconButton(onClick = { viewModel.cargarNotificaciones() }) {
                    Icon(Icons.Filled.Refresh, "Recargar", tint = Color(0xFF1A365D))
                }
            }
        }

        when {
            SessionManager.isGuest -> {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                        Icon(Icons.Filled.Lock, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Inicia sesión para ver tus notificaciones.",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            state.isLoading -> {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF1A365D))
                }
            }

            state.notificaciones.isEmpty() -> {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                        Icon(Icons.Filled.NotificationsNone, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(56.dp))
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Sin notificaciones aún",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A365D),
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Cuando un reclutador actualice el estado de tu postulación, aparecerá aquí.",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.notificaciones, key = { it.id }) { notif ->
                        NotificacionCard(
                            notificacion = notif,
                            onMarcarLeida = { viewModel.marcarLeida(notif.id) }
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun NotificacionCard(
    notificacion: Notificacion,
    onMarcarLeida: () -> Unit
) {
    val bgColor = if (notificacion.leida) Color.White else Color(0xFFEFF6FF)
    val iconInfo = when {
        notificacion.titulo.contains("aceptado", ignoreCase = true) ->
            Icons.Filled.CheckCircle to Color(0xFF059669)
        notificacion.titulo.contains("rechazado", ignoreCase = true) ||
                notificacion.titulo.contains("no seleccionada", ignoreCase = true) ->
            Icons.Filled.Cancel to Color(0xFFDC2626)
        notificacion.titulo.contains("revisión", ignoreCase = true) ->
            Icons.Filled.Search to Color(0xFFF59E0B)
        else ->
            Icons.Filled.Notifications to Color(0xFF1A365D)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !notificacion.leida) { onMarcarLeida() },
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (notificacion.leida) 0.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(iconInfo.second.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconInfo.first,
                    contentDescription = null,
                    tint = iconInfo.second,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        notificacion.titulo,
                        fontSize = 14.sp,
                        fontWeight = if (notificacion.leida) FontWeight.Normal else FontWeight.Bold,
                        color = Color(0xFF1A365D),
                        modifier = Modifier.weight(1f)
                    )
                    if (!notificacion.leida) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF3B82F6))
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    notificacion.mensaje,
                    fontSize = 13.sp,
                    color = Color(0xFF475569),
                    lineHeight = 18.sp
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    notificacion.fechaCreacion.formatFechaNot(),
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
                if (!notificacion.leida) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Toca para marcar como leída",
                        fontSize = 11.sp,
                        color = Color(0xFF3B82F6),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

private fun String.formatFechaNot(): String {
    if (this.isBlank()) return ""
    return try {
        val limpio = this.replace("Z", "").replace("T", " ").take(16)
        val partes = limpio.split(" ")
        val fecha = partes[0]
        val hora  = partes.getOrNull(1) ?: ""
        val meses = listOf("","ene","feb","mar","abr","may","jun","jul","ago","sep","oct","nov","dic")
        val (anio, mes, dia) = fecha.split("-")
        val mesNombre = meses.getOrElse(mes.toIntOrNull() ?: 0) { mes }
        if (hora.isNotBlank()) "$dia $mesNombre $anio • $hora" else "$dia $mesNombre $anio"
    } catch (e: Exception) { this.take(10) }
}
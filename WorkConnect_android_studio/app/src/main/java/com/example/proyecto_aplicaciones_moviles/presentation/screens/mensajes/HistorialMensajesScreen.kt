package com.example.proyecto_aplicaciones_moviles.presentation.screens.mensajes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto_aplicaciones_moviles.core.utils.SessionManager

@Composable
fun HistorialMensajesScreen(
    viewModel: ChatListViewModel,
    onNavigateToChat: (conversationId: String, nombreOtroParticipante: String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val userId = SessionManager.currentUserId

    if (userId.isNullOrBlank()) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA)),
            contentAlignment = Alignment.Center
        ) {
            Text("Inicia sesión para ver los mensajes", color = Color.Gray)
        }
        return
    }

    LaunchedEffect(Unit) {
        viewModel.cargarConversaciones()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        Text(
            text = "Mensajes",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A365D),
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)
        )

        if (state.isLoading && state.conversaciones.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF1A365D))
            }
            return@Column
        }

        if (state.errorMessage != null && state.conversaciones.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(state.errorMessage!!, color = Color(0xFF991B1B), fontSize = 14.sp)
            }
            return@Column
        }

        if (state.conversaciones.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay conversaciones aún", color = Color.Gray)
            }
            return@Column
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.conversaciones) { conv ->
                val ultimoMensaje = state.ultimoMensajePorConversacion[conv.id]
                val tieneNoLeido = ultimoMensaje != null && !ultimoMensaje.leido && !ultimoMensaje.esMio

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToChat(conv.id, conv.nombreOtroParticipante) },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar inicial
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color(0xFF1A365D),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = conv.nombreOtroParticipante
                                        .firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }
                        }

                        Spacer(Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = conv.nombreOtroParticipante,
                                fontWeight = if (tieneNoLeido) FontWeight.Bold else FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = Color(0xFF1A365D)
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = ultimoMensaje?.contenido ?: conv.fechaCreacion.take(10),
                                fontSize = 12.sp,
                                color = if (tieneNoLeido) Color(0xFF1A365D) else Color.Gray,
                                fontWeight = if (tieneNoLeido) FontWeight.Medium else FontWeight.Normal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(Modifier.width(8.dp))

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = ultimoMensaje?.fechaEnvio?.horaCorta() ?: "",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                            if (tieneNoLeido) {
                                Spacer(Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(Color(0xFF1A365D), CircleShape)
                                )
                            }
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

private fun String.horaCorta(): String {
    val tIdx = indexOf('T')
    return if (tIdx >= 0 && length >= tIdx + 6) substring(tIdx + 1, tIdx + 6) else ""
}

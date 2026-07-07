package com.example.proyecto_aplicaciones_moviles.presentation.screens.mensajes

import com.example.proyecto_aplicaciones_moviles.domain.model.Conversacion
import com.example.proyecto_aplicaciones_moviles.domain.model.Mensaje

data class ChatListState(
    val isLoading: Boolean = false,
    val conversaciones: List<Conversacion> = emptyList(),
    val errorMessage: String? = null,
    val ultimoMensajePorConversacion: Map<String, Mensaje?> = emptyMap(),
    val hasUnreadMensajes: Boolean = false
)

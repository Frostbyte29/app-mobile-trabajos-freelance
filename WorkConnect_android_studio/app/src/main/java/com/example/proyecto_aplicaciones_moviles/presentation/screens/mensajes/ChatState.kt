package com.example.proyecto_aplicaciones_moviles.presentation.screens.mensajes

import com.example.proyecto_aplicaciones_moviles.domain.model.Mensaje

data class ChatState(
    val isLoading: Boolean = false,
    val mensajes: List<Mensaje> = emptyList(),
    val nombreOtroParticipante: String = "",
    val isEnviando: Boolean = false,
    val errorMessage: String? = null
)

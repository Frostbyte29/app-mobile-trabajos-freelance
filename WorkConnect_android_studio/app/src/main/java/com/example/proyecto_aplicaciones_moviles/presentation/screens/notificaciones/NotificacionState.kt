package com.example.proyecto_aplicaciones_moviles.presentation.screens.notificaciones

import com.example.proyecto_aplicaciones_moviles.domain.model.Notificacion

data class NotificacionState(
    val isLoading: Boolean = false,
    val notificaciones: List<Notificacion> = emptyList(),
    val errorMessage: String? = null
)
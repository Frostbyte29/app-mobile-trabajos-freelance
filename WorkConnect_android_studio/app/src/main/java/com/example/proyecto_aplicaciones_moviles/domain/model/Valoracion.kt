package com.example.proyecto_aplicaciones_moviles.domain.model

data class Valoracion(
    val id: String,
    val usuarioEmisorId: String,
    val usuarioReceptorId: String,
    val proyectoId: String?,
    val puntuacion: Int,           // 1 a 5
    val comentario: String,
    val fechaCreacion: String,
    val editada: Boolean = false,
    // Enriquecido — nombre del emisor para mostrar en la UI
    val nombreEmisor: String = ""
)

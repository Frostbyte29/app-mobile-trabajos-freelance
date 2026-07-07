package com.example.proyecto_aplicaciones_moviles.domain.model

data class Valoracion(
    val id: String,
    val usuarioEmisorId: String,
    val usuarioReceptorId: String,
    val proyectoId: String?,
    val puntuacion: Int,
    val comentario: String,
    val fechaCreacion: String,
    val editada: Boolean = false,
    val nombreEmisor: String = ""
)

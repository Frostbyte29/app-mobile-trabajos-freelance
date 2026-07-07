package com.example.proyecto_aplicaciones_moviles.domain.model

data class Notificacion(
    val id: String,
    val usuarioId: String,
    val titulo: String,
    val mensaje: String,
    val tipo: String,
    val referenciaId: String?,
    val leida: Boolean,
    val fechaCreacion: String
)

package com.example.proyecto_aplicaciones_moviles.domain.model

data class Mensaje(
    val id: String,
    val emisorId: String,
    val contenido: String,
    val fechaEnvio: String,
    val esMio: Boolean,
    val leido: Boolean = false
)

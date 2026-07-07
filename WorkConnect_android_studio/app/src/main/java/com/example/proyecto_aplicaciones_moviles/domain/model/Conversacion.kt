package com.example.proyecto_aplicaciones_moviles.domain.model

data class Conversacion(
    val id: String,
    val otroParticipanteId: String,
    val nombreOtroParticipante: String,
    val vacanteId: String? = null,
    val activa: Boolean = true,
    val fechaCreacion: String = ""
)

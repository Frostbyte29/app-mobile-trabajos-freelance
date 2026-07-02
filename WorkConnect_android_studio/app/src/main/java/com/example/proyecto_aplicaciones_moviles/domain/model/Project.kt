package com.example.proyecto_aplicaciones_moviles.domain.model

// Tipo de oferta publicada
// "trabajo"  → reclutador busca candidatos (muestra empresa)
// "servicio" → freelancer ofrece sus servicios (muestra nombre personal)
enum class TipoOferta { TRABAJO, SERVICIO }

data class Project(
    val id: String = "",
    val title: String,
    val description: String,
    val budget: Double,
    val category: String,
    val company: String,        // empresa si es TRABAJO, nombre personal si es SERVICIO
    val tipoOferta: TipoOferta = TipoOferta.TRABAJO,
    val creadoPorId: String? = null,
    val createdAt: String = ""  // ISO 8601 — fecha y hora de publicación
)

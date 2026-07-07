package com.example.proyecto_aplicaciones_moviles.domain.model

enum class TipoOferta { TRABAJO, SERVICIO }

data class Proyecto(
    val id: String = "",
    val title: String,
    val description: String,
    val budget: Double,
    val category: String,
    val company: String,
    val tipoOferta: TipoOferta = TipoOferta.TRABAJO,
    val creadoPorId: String? = null,
    val createdAt: String = ""
)
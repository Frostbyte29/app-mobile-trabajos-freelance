package com.example.proyecto_aplicaciones_moviles.domain.model

data class Contrato(
    val id: String,
    val contratanteId: String,
    val freelancerId: String,
    val ofertaId: String,
    val tituloOferta: String,
    val tipoOrigen: String,
    val estado: String,
    val fechaInicio: String,
    val fechaFin: String?,
    val postulacionId: String?
)

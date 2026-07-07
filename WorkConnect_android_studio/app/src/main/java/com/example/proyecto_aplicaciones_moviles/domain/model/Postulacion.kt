package com.example.proyecto_aplicaciones_moviles.domain.model

// Modelo puro que usa la UI — sin dependencias de DTOs ni de AWS
data class Postulacion(
    val id: String,
    val candidatoId: String,
    val vacanteId: String,
    val mensajePresentacion: String,
    val estado: String,
    val fechaPostulacion: String,
    val tituloVacante: String = "",
    val nombreCandidato: String = "",
    val linkedinUrl: String? = null,
    val repoUrl: String? = null
)

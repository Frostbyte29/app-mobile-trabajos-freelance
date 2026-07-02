package com.example.proyecto_aplicaciones_moviles.domain.model

// Modelo puro que usa la UI — sin dependencias de DTOs ni de AWS
data class Postulacion(
    val id: String,
    val candidatoId: String,
    val vacanteId: String,
    val mensajePresentacion: String,
    val estado: String,           // "postulado", "en_revision", "aceptado", "rechazado"
    val fechaPostulacion: String,
    // Campos enriquecidos — se resuelven en el repositorio para evitar mostrar IDs en la UI
    val tituloVacante: String = "",    // título del proyecto al que se postuló
    val nombreCandidato: String = ""   // nombre completo del candidato que se postuló
)

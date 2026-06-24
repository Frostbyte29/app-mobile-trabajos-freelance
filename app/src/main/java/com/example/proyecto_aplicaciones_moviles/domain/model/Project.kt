package com.example.proyecto_aplicaciones_moviles.domain.model

// Este es el modelo puro que usará la interfaz gráfica
data class Project(
    val id: String = "",
    val title: String,
    val description: String,
    val budget: Double,
    val category: String,
    val company: String
)
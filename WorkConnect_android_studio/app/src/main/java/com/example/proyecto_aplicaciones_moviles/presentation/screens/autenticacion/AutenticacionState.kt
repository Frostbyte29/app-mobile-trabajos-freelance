package com.example.proyecto_aplicaciones_moviles.presentation.screens.autenticacion

data class AutenticacionState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
package com.example.proyecto_aplicaciones_moviles.presentation.auth

data class AuthState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
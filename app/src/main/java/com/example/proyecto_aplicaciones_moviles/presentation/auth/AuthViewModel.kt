package com.example.proyecto_aplicaciones_moviles.presentation.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {

    // Estado interno mutable (privado)
    private val _state = MutableStateFlow(AuthState())
    // Estado expuesto hacia la UI (de solo lectura)
    val state: StateFlow<AuthState> = _state.asStateFlow()

    // Funciones de validación
    fun validateEmail(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun validatePassword(password: String): Boolean {
        return password.length >= 8
    }

    // Función para simular el inicio de sesión
    fun loginUser(email: String, password: String, onNavigateToHome: () -> Unit) {
        if (!validateEmail(email)) {
            _state.update { it.copy(errorMessage = "El correo electrónico no es válido.") }
            return
        }

        if (!validatePassword(password)) {
            _state.update { it.copy(errorMessage = "La contraseña debe tener al menos 8 caracteres.") }
            return
        }

        // Si pasa las validaciones, disparamos el proceso
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            delay(2000) // Simulamos una espera de red de 2 segundos

            _state.update { it.copy(isLoading = false, isSuccess = true) }
            onNavigateToHome()
        }
    }

    // Función para limpiar errores de la pantalla
    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }
}
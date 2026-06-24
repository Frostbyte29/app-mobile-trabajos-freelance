package com.example.proyecto_aplicaciones_moviles.presentation.screens.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    fun validateEmail(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun validatePassword(password: String): Boolean {
        return password.length >= 8
    }

    fun loginUser(email: String, password: String, onNavigateToHome: () -> Unit) {
        if (!validateEmail(email)) {
            _state.update { it.copy(errorMessage = "El correo electrónico no es válido.") }
            return
        }

        if (!validatePassword(password)) {
            _state.update { it.copy(errorMessage = "La contraseña debe tener al menos 8 caracteres.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            delay(2000)
            _state.update { it.copy(isLoading = false, isSuccess = true) }
            onNavigateToHome()
        }
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }

    // Función para simular el registro de un nuevo usuario
    fun registerUser(
        fullName: String,
        email: String,
        password: String,
        termsAccepted: Boolean,
        onNavigateToLogin: () -> Unit
    ) {
        if (fullName.isBlank()) {
            _state.update { it.copy(errorMessage = "El nombre no puede estar vacío.") }
            return
        }

        if (!validateEmail(email)) {
            _state.update { it.copy(errorMessage = "El correo electrónico no es válido.") }
            return
        }

        if (!validatePassword(password)) {
            _state.update { it.copy(errorMessage = "La contraseña debe tener al menos 8 caracteres.") }
            return
        }

        if (!termsAccepted) {
            _state.update { it.copy(errorMessage = "Debes aceptar los Términos de Servicio para continuar.") }
            return
        }

        // Si todo está correcto, simulamos la creación de la cuenta en el servidor
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            delay(2000) // Simula la espera de 2 segundos de internet

            _state.update { it.copy(isLoading = false, isSuccess = true) }
            onNavigateToLogin() // Lo mandamos de regreso al Login para que inicie sesión
        }
    }
}
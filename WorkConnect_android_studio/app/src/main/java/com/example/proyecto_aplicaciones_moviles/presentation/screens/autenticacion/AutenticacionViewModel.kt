package com.example.proyecto_aplicaciones_moviles.presentation.screens.autenticacion

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_aplicaciones_moviles.data.remote.UsuarioRequestDto
import com.example.proyecto_aplicaciones_moviles.domain.repository.AutenticacionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AutenticacionViewModel(
    private val repository: AutenticacionRepository // Conexión a AWS inyectada
) : ViewModel() {

    private val _state = MutableStateFlow(AutenticacionState())
    val state: StateFlow<AutenticacionState> = _state.asStateFlow()

    fun validateEmail(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun validatePassword(password: String): Boolean {
        return password.length >= 8
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }

    // --- LOGIN (Validación Híbrida conectada a AWS) ---
    fun LoginUsuario(email: String, password: String, onNavigateToHome: () -> Unit) {
        if (!validateEmail(email)) {
            _state.update { it.copy(errorMessage = "El correo electrónico no es válido.") }
            return
        }
        if (!validatePassword(password)) {
            _state.update { it.copy(errorMessage = "La contraseña debe tener al menos 8 caracteres.") }
            return
        }

        viewModelScope.launch {
            // Mostramos la ruedita de carga
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            // ¡Vamos a AWS a preguntar si el correo existe!
            val emailExiste = repository.verificarEmail(email)

            if (emailExiste) {
                // ¡Éxito! El correo está en la base de datos
                _state.update { it.copy(isLoading = false, isSuccess = true) }
                onNavigateToHome()
            } else {
                // El correo no existe en AWS
                _state.update { it.copy(isLoading = false, errorMessage = "Credenciales incorrectas o usuario no registrado.") }
            }
        }
    }


    // --- REGISTRO (Conectado a AWS DynamoDB) ---
    fun RegistrarUsuario(
        fullName: String,
        email: String,
        password: String,
        roleId: Int, // ¡NUEVO! Recibimos el número de la tarjeta (1 o 2)
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

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val partesDelNombre = fullName.trim().split(" ", limit = 2)
            val nombres = partesDelNombre.getOrNull(0) ?: ""
            val apellidos = partesDelNombre.getOrNull(1) ?: ""

            // ¡LA MAGIA ESTÁ AQUÍ!
            // Si roleId es 1, es candidato. Si es 2, es reclutador.
            val rolElegido = if (roleId == 1) "candidato" else "reclutador"

            val request = UsuarioRequestDto(
                nombres = nombres,
                apellidos = apellidos,
                correo = email,
                telefono = "+51000000000",
                roles = listOf(rolElegido) // Enviamos el rol dinámico a AWS
            )

            val guardadoExitoso = repository.registrarCandidato(request)

            if (guardadoExitoso) {
                _state.update { it.copy(isLoading = false, isSuccess = true) }
                onNavigateToLogin()
            } else {
                _state.update { it.copy(isLoading = false, errorMessage = "Error de conexión con el servidor. Inténtalo de nuevo.") }
            }
        }
    }
}
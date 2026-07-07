package com.example.proyecto_aplicaciones_moviles.presentation.screens.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_aplicaciones_moviles.data.remote.UsuarioRequestDto
import com.example.proyecto_aplicaciones_moviles.domain.repository.AutenticacionRepository
import com.example.proyecto_aplicaciones_moviles.presentation.screens.autenticacion.AutenticacionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AutenticacionViewModel(
    private val repository: AutenticacionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AutenticacionState())
    val state: StateFlow<AutenticacionState> = _state.asStateFlow()

    fun validarCorreo(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun validarContrasena(password: String): Boolean {
        return password.length >= 8
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }

    fun iniciarSesion(email: String, password: String, onNavigateToHome: () -> Unit) {
        if (!validarCorreo(email)) {
            _state.update { it.copy(errorMessage = "El correo electrónico no es válido.") }
            return
        }
        if (!validarContrasena(password)) {
            _state.update { it.copy(errorMessage = "La contraseña debe tener al menos 8 caracteres.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val emailExiste = repository.verificarCorreoExiste(email)

            if (emailExiste) {
                _state.update { it.copy(isLoading = false, isSuccess = true) }
                onNavigateToHome()
            } else {
                _state.update { it.copy(isLoading = false, errorMessage = "Credenciales incorrectas o usuario no registrado.") }
            }
        }
    }

    fun registrarUsuario(
        fullName: String,
        email: String,
        password: String,
        roleId: Int,
        termsAccepted: Boolean,
        onNavigateToLogin: () -> Unit
    ) {
        if (fullName.isBlank()) {
            _state.update { it.copy(errorMessage = "El nombre no puede estar vacío.") }
            return
        }
        if (!validarCorreo(email)) {
            _state.update { it.copy(errorMessage = "El correo electrónico no es válido.") }
            return
        }
        if (!validarContrasena(password)) {
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
            val apellidos = partesDelNombre.getOrNull(1)?.takeIf { it.isNotBlank() } ?: "-"
            val rolElegido = if (roleId == 1) "candidato" else "reclutador"

            val request = UsuarioRequestDto(
                nombres = nombres,
                apellidos = apellidos,
                correo = email,
                telefono = "+51000000000",
                roles = listOf(rolElegido)
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
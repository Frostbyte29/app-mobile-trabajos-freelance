package com.example.proyecto_aplicaciones_moviles.presentation.screens.mensajes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_aplicaciones_moviles.core.utils.SessionManager
import com.example.proyecto_aplicaciones_moviles.domain.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel(
    private val repository: ChatRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state.asStateFlow()

    fun cargarMensajes(conversacionId: String, nombreOtroParticipante: String) {
        val usuarioId = SessionManager.currentUserId ?: return
        _state.update { it.copy(nombreOtroParticipante = nombreOtroParticipante) }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val mensajes = repository.obtenerMensajes(conversacionId, usuarioId)
                _state.update { it.copy(isLoading = false, mensajes = mensajes) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = "No se pudieron cargar los mensajes") }
            }
        }
    }

    fun actualizarMensajesSilencioso(conversacionId: String) {
        val usuarioId = SessionManager.currentUserId ?: return
        viewModelScope.launch {
            try {
                val mensajes = repository.obtenerMensajes(conversacionId, usuarioId)
                _state.update { it.copy(mensajes = mensajes) }
            } catch (_: Exception) {}
        }
    }

    fun enviarMensaje(conversacionId: String, texto: String) {
        if (texto.isBlank()) return
        val emisorId = SessionManager.currentUserId ?: return

        viewModelScope.launch {
            _state.update { it.copy(isEnviando = true, errorMessage = null) }
            try {
                val ok = repository.enviarMensaje(conversacionId, emisorId, texto)
                if (ok) {
                    _state.update { it.copy(isEnviando = false) }
                    actualizarMensajesSilencioso(conversacionId)
                } else {
                    _state.update { it.copy(isEnviando = false, errorMessage = "No se pudo enviar el mensaje") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isEnviando = false, errorMessage = "No se pudo enviar el mensaje") }
            }
        }
    }
}

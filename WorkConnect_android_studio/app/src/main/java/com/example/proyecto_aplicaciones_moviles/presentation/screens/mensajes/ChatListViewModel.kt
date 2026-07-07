package com.example.proyecto_aplicaciones_moviles.presentation.screens.mensajes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_aplicaciones_moviles.core.utils.SessionManager
import com.example.proyecto_aplicaciones_moviles.domain.repository.ChatRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatListViewModel(
    private val repository: ChatRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ChatListState())
    val state: StateFlow<ChatListState> = _state.asStateFlow()

    fun cargarConversaciones() {
        val usuarioId = SessionManager.currentUserId
        if (usuarioId.isNullOrEmpty()) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val lista = repository.obtenerConversaciones(usuarioId)

                val previews = lista.map { conv ->
                    async {
                        try {
                            val mensajes = repository.obtenerMensajes(conv.id, usuarioId)
                            conv.id to mensajes.lastOrNull()
                        } catch (e: Exception) {
                            conv.id to null
                        }
                    }
                }.awaitAll().toMap()

                val hayNoLeidos = previews.values.any { msg ->
                    msg != null && !msg.leido && !msg.esMio
                }

                _state.update {
                    it.copy(
                        isLoading = false,
                        conversaciones = lista,
                        ultimoMensajePorConversacion = previews,
                        hasUnreadMensajes = hayNoLeidos
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = "No se pudieron cargar los mensajes") }
            }
        }
    }

    fun marcarLeida(conversationId: String) {
        val mensajeActual = _state.value.ultimoMensajePorConversacion[conversationId] ?: return
        val actualizado = _state.value.ultimoMensajePorConversacion.toMutableMap()
        actualizado[conversationId] = mensajeActual.copy(leido = true)
        val hayNoLeidos = actualizado.values.any { msg -> msg != null && !msg.leido && !msg.esMio }
        _state.update { it.copy(ultimoMensajePorConversacion = actualizado, hasUnreadMensajes = hayNoLeidos) }
    }
}

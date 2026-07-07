package com.example.proyecto_aplicaciones_moviles.presentation.screens.perfilpublico

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_aplicaciones_moviles.core.utils.SessionManager
import com.example.proyecto_aplicaciones_moviles.domain.repository.ChatRepository
import com.example.proyecto_aplicaciones_moviles.domain.repository.ContratoRepository
import com.example.proyecto_aplicaciones_moviles.domain.repository.PerfilRepository
import com.example.proyecto_aplicaciones_moviles.domain.repository.ValoracionRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PerfilPublicoViewModel(
    private val perfilRepository: PerfilRepository,
    private val valoracionRepository: ValoracionRepository,
    private val contratoRepository: ContratoRepository,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PerfilPublicoState())
    val state: StateFlow<PerfilPublicoState> = _state.asStateFlow()

    fun cargarPerfil(usuarioId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val usuarioDeferred = async { perfilRepository.obtenerUsuarioPorId(usuarioId) }
                val valoracionesDeferred = async { valoracionRepository.obtenerValoracionesPorUsuario(usuarioId) }
                val contratosDeferred = async { contratoRepository.obtenerContratosFinalizadosComoFreelancer(usuarioId) }

                val usuario = usuarioDeferred.await()
                val valoraciones = valoracionesDeferred.await()
                val contratos = contratosDeferred.await()

                if (usuario == null) {
                    _state.update { it.copy(isLoading = false, errorMessage = "No se pudo cargar el perfil.") }
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            usuario = usuario,
                            valoraciones = valoraciones,
                            trabajosRealizados = contratos
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = "Error al cargar el perfil.") }
            }
        }
    }

    fun iniciarChat(otroUsuarioId: String) {
        val miId = SessionManager.currentUserId ?: return
        viewModelScope.launch {
            _state.update { it.copy(isBuscandoChat = true, errorMessage = null) }
            try {
                val conv = chatRepository.obtenerOCrearConversacion(miId, otroUsuarioId)
                if (conv != null) {
                    _state.update {
                        it.copy(
                            isBuscandoChat = false,
                            chatNavConvId = conv.id,
                            chatNavNombre = conv.nombreOtroParticipante
                        )
                    }
                } else {
                    _state.update { it.copy(isBuscandoChat = false, errorMessage = "No se pudo iniciar el chat.") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isBuscandoChat = false, errorMessage = "No se pudo iniciar el chat.") }
            }
        }
    }

    fun clearChatNav() = _state.update { it.copy(chatNavConvId = null, chatNavNombre = null) }
}

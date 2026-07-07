package com.example.proyecto_aplicaciones_moviles.presentation.screens.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_aplicaciones_moviles.core.utils.SessionManager
import com.example.proyecto_aplicaciones_moviles.domain.model.Proyecto
import com.example.proyecto_aplicaciones_moviles.domain.model.TipoOferta
import com.example.proyecto_aplicaciones_moviles.domain.repository.ChatRepository
import com.example.proyecto_aplicaciones_moviles.domain.repository.ContratoRepository
import com.example.proyecto_aplicaciones_moviles.domain.repository.PostulacionRepository
import com.example.proyecto_aplicaciones_moviles.domain.repository.ValoracionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetalleProyectoViewModel(
    private val postulacionRepository: PostulacionRepository,
    private val valoracionRepository: ValoracionRepository,
    private val contratoRepository: ContratoRepository,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DetalleProyectoState())
    val state: StateFlow<DetalleProyectoState> = _state.asStateFlow()

    fun cargarDetalle(proyecto: Proyecto) {
        _state.update { it.copy(proyecto = proyecto) }
        cargarValoraciones(proyecto.id, proyecto.creadoPorId)
        val esPropio = proyecto.creadoPorId != null &&
                proyecto.creadoPorId == SessionManager.currentUserId
        if (esPropio && proyecto.tipoOferta == TipoOferta.TRABAJO) {
            cargarPostulantes(proyecto.id)
        }
    }

    private fun cargarPostulantes(vacanteId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingPostulantes = true) }
            try {
                val lista = postulacionRepository.obtenerPostulacionesVacante(vacanteId)
                _state.update { it.copy(isLoadingPostulantes = false, postulaciones = lista) }
            } catch (_: Exception) {
                _state.update { it.copy(isLoadingPostulantes = false) }
            }
        }
    }

    private fun cargarValoraciones(proyectoId: String, usuarioId: String?) {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingValoraciones = true) }
            val lista = if (usuarioId != null) {
                valoracionRepository.obtenerValoracionesPorUsuario(usuarioId)
                    .filter { it.proyectoId == proyectoId }
            } else emptyList()
            _state.update { it.copy(isLoadingValoraciones = false, valoraciones = lista) }
        }
    }

    fun abrirFormPostulacion() =
        _state.update { it.copy(mostrarFormPostulacion = true, mensajePostulacion = "", editLinkedinUrl = "", editRepoUrl = "", errorMessage = null) }

    fun cerrarFormPostulacion() =
        _state.update { it.copy(mostrarFormPostulacion = false) }

    fun onMensajePostulacionChange(v: String) =
        _state.update { it.copy(mensajePostulacion = v) }

    fun onEditLinkedinUrlChange(v: String) =
        _state.update { it.copy(editLinkedinUrl = v) }

    fun onEditRepoUrlChange(v: String) =
        _state.update { it.copy(editRepoUrl = v) }

    fun enviarPostulacion() {
        val project = _state.value.proyecto ?: return
        val userId = SessionManager.currentUserId ?: return

        viewModelScope.launch {
            _state.update { it.copy(isPostulando = true, errorMessage = null) }
            val exito = postulacionRepository.crearPostulacion(
                candidatoId = userId,
                vacanteId = project.id,
                mensajePresentacion = _state.value.mensajePostulacion,
                linkedinUrl = _state.value.editLinkedinUrl.takeIf { it.isNotBlank() },
                repoUrl = _state.value.editRepoUrl.takeIf { it.isNotBlank() }
            )
            if (exito) {
                _state.update {
                    it.copy(
                        isPostulando = false,
                        mostrarFormPostulacion = false,
                        postulacionExitosa = true
                    )
                }
            } else {
                _state.update {
                    it.copy(isPostulando = false, errorMessage = "No se pudo enviar la postulación.")
                }
            }
        }
    }

    fun contratarServicio() {
        val proyecto = _state.value.proyecto ?: return
        val contratanteId = SessionManager.currentUserId ?: return
        val freelancerId = proyecto.creadoPorId ?: return

        viewModelScope.launch {
            _state.update { it.copy(isContratando = true, errorMessage = null) }
            val exito = contratoRepository.crearContrato(
                contratanteId = contratanteId,
                freelancerId = freelancerId,
                ofertaId = proyecto.id,
                tituloOferta = proyecto.title,
                tipoOrigen = "servicio"
            )
            if (exito) {
                _state.update { it.copy(isContratando = false, contratoExitoMsg = "¡Contrato creado! Encontralo en tu historial de actividad.") }
            } else {
                _state.update { it.copy(isContratando = false, errorMessage = "No se pudo crear el contrato. Intentá de nuevo.") }
            }
        }
    }

    fun clearContratoExito() = _state.update { it.copy(contratoExitoMsg = null) }

    fun clearPostulacionExitosa() = _state.update { it.copy(postulacionExitosa = false) }
    fun clearError() = _state.update { it.copy(errorMessage = null) }

    fun iniciarChat() {
        val proyecto = _state.value.proyecto ?: return
        val usuarioId = SessionManager.currentUserId ?: return
        val otroUsuarioId = proyecto.creadoPorId ?: return

        viewModelScope.launch {
            _state.update { it.copy(isBuscandoChat = true, errorMessage = null) }
            try {
                val conv = chatRepository.obtenerOCrearConversacion(
                    usuarioId = usuarioId,
                    otroUsuarioId = otroUsuarioId,
                    vacanteId = proyecto.id
                )
                if (conv != null) {
                    _state.update {
                        it.copy(
                            isBuscandoChat = false,
                            chatNavConvId = conv.id,
                            chatNavNombre = conv.nombreOtroParticipante
                        )
                    }
                } else {
                    _state.update { it.copy(isBuscandoChat = false, errorMessage = "No se pudo iniciar el chat") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isBuscandoChat = false, errorMessage = "No se pudo iniciar el chat") }
            }
        }
    }

    fun clearChatNav() = _state.update { it.copy(chatNavConvId = null, chatNavNombre = null) }
}
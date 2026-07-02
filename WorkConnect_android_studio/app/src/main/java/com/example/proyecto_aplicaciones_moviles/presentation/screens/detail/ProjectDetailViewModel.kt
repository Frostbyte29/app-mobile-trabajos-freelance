package com.example.proyecto_aplicaciones_moviles.presentation.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_aplicaciones_moviles.core.utils.SessionManager
import com.example.proyecto_aplicaciones_moviles.domain.model.Project
import com.example.proyecto_aplicaciones_moviles.domain.repository.PostulacionRepository
import com.example.proyecto_aplicaciones_moviles.domain.repository.ValoracionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProjectDetailViewModel(
    private val postulacionRepository: PostulacionRepository,
    private val valoracionRepository: ValoracionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProjectDetailState())
    val state: StateFlow<ProjectDetailState> = _state.asStateFlow()

    // Carga el detalle de un proyecto y sus valoraciones filtradas por proyectoId
    fun cargarDetalle(project: Project) {
        _state.update { it.copy(project = project) }
        cargarValoraciones(project.id, project.creadoPorId)
    }

    private fun cargarValoraciones(proyectoId: String, usuarioId: String?) {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingValoraciones = true) }
            // Si hay creadoPorId cargamos todas las del receptor y filtramos por proyectoId
            val lista = if (usuarioId != null) {
                valoracionRepository.obtenerValoracionesPorUsuario(usuarioId)
                    .filter { it.proyectoId == proyectoId }
            } else emptyList()
            _state.update { it.copy(isLoadingValoraciones = false, valoraciones = lista) }
        }
    }

    // ── POSTULACIÓN ──────────────────────────────────────────────────────

    fun abrirFormPostulacion() =
        _state.update { it.copy(mostrarFormPostulacion = true, mensajePostulacion = "", errorMessage = null) }

    fun cerrarFormPostulacion() =
        _state.update { it.copy(mostrarFormPostulacion = false) }

    fun onMensajePostulacionChange(v: String) =
        _state.update { it.copy(mensajePostulacion = v) }

    fun enviarPostulacion() {
        val project = _state.value.project ?: return
        val userId = SessionManager.currentUserId ?: return

        viewModelScope.launch {
            _state.update { it.copy(isPostulando = true, errorMessage = null) }
            val exito = postulacionRepository.crearPostulacion(
                candidatoId = userId,
                vacanteId = project.id,
                mensajePresentacion = _state.value.mensajePostulacion
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

    // ── VALORACIÓN ───────────────────────────────────────────────────────

    fun abrirFormValoracion() =
        _state.update { it.copy(mostrarFormValoracion = true, comentarioNuevo = "", puntuacionSeleccionada = 5, errorMessage = null) }

    fun cerrarFormValoracion() =
        _state.update { it.copy(mostrarFormValoracion = false) }

    fun onPuntuacionChange(p: Int) =
        _state.update { it.copy(puntuacionSeleccionada = p) }

    fun onComentarioChange(v: String) =
        _state.update { it.copy(comentarioNuevo = v) }

    fun enviarValoracion() {
        val project = _state.value.project ?: return
        val emisorId = SessionManager.currentUserId ?: return
        val receptorId = project.creadoPorId ?: return
        val s = _state.value

        if (s.comentarioNuevo.isBlank()) {
            _state.update { it.copy(errorMessage = "Escribe un comentario antes de enviar.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isEnviandoValoracion = true, errorMessage = null) }
            val exito = valoracionRepository.crearValoracion(
                emisorId = emisorId,
                receptorId = receptorId,
                proyectoId = project.id,
                puntuacion = s.puntuacionSeleccionada,
                comentario = s.comentarioNuevo
            )
            if (exito) {
                _state.update {
                    it.copy(
                        isEnviandoValoracion = false,
                        mostrarFormValoracion = false,
                        valoracionEnviadaExito = true
                    )
                }
                // Recargamos valoraciones para mostrar la nueva
                val proyectoId = project.id
                cargarValoraciones(proyectoId, receptorId)
            } else {
                _state.update {
                    it.copy(isEnviandoValoracion = false, errorMessage = "No se pudo enviar el comentario.")
                }
            }
        }
    }

    fun clearPostulacionExitosa() = _state.update { it.copy(postulacionExitosa = false) }
    fun clearValoracionExitosa() = _state.update { it.copy(valoracionEnviadaExito = false) }
    fun clearError() = _state.update { it.copy(errorMessage = null) }
}

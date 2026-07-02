package com.example.proyecto_aplicaciones_moviles.presentation.screens.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_aplicaciones_moviles.core.utils.SessionManager
import com.example.proyecto_aplicaciones_moviles.domain.model.Postulacion
import com.example.proyecto_aplicaciones_moviles.domain.repository.PostulacionRepository
import com.example.proyecto_aplicaciones_moviles.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MyActivityViewModel(
    private val postulacionRepository: PostulacionRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MyActivityState())
    val state: StateFlow<MyActivityState> = _state.asStateFlow()

    init {
        cargarActividad()
    }

    // Carga postulaciones y/o proyectos según los roles reales del usuario
    fun cargarActividad() {
        val email = SessionManager.currentUserEmail ?: return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val usuario = profileRepository.obtenerUsuarioPorCorreo(email)
            if (usuario == null) {
                _state.update { it.copy(isLoading = false, errorMessage = "No se pudo cargar tu actividad.") }
                return@launch
            }

            val roles = usuario.roles ?: emptyList()
            val userId = usuario.id ?: ""

            // Guardamos userId, empresa y nombre completo en SessionManager
            if (userId.isNotBlank()) SessionManager.setUserId(userId)
            // Solo guardamos empresaNombre si el usuario tiene rol reclutador
            // para no contaminar el nombre visible cuando publica como candidato
            if (roles.contains("reclutador")) {
                usuario.empresaInfo?.nombre?.takeIf { it.isNotBlank() }
                    ?.let { SessionManager.setEmpresaNombre(it) }
            }
            val nombreCompleto = "${usuario.nombres ?: ""} ${usuario.apellidos ?: ""}".trim()
            if (nombreCompleto.isNotBlank()) SessionManager.setNombreCompleto(nombreCompleto)

            // Seteamos el rol activo por defecto si todavía no está definido
            // (primera carga tras login). Así el filtro de HomeScreen funciona
            // correctamente desde el primer momento.
            if (SessionManager.activeRole == null || !roles.contains(SessionManager.activeRole)) {
                val rolDefault = roles.firstOrNull() ?: "candidato"
                SessionManager.switchRole(rolDefault)
            }

            // Cargamos postulaciones si tiene rol candidato
            val postulaciones = if (roles.contains("candidato") && userId.isNotBlank()) {
                postulacionRepository.obtenerPostulacionesCandidato(userId)
            } else emptyList()

            // Cargamos proyectos publicados si tiene rol reclutador
            val misProyectos = if (roles.contains("reclutador") && userId.isNotBlank()) {
                postulacionRepository.obtenerProyectosReclutador(userId)
            } else emptyList()

            // Tab inicial: respetamos el rol activo en SessionManager
            val tabInicial = when {
                SessionManager.activeRole != null && roles.contains(SessionManager.activeRole) ->
                    SessionManager.activeRole!!
                roles.isNotEmpty() -> roles.first()
                else -> "candidato"
            }

            _state.update {
                it.copy(
                    isLoading = false,
                    roles = roles,
                    userId = userId,
                    postulaciones = postulaciones,
                    misProyectos = misProyectos,
                    tabActivo = tabInicial
                )
            }
        }
    }

    // Cambia el tab activo (candidato ↔ reclutador)
    fun setTabActivo(tab: String) {
        _state.update { it.copy(tabActivo = tab) }
    }

    // ── CANDIDATO: postularse a una vacante desde HomeScreen ──────────────

    fun postularse(vacanteId: String, mensajePresentacion: String) {
        // Usamos el userId del state si ya cargó, sino el de SessionManager como fallback
        val userId = _state.value.userId.ifBlank {
            SessionManager.currentUserId ?: ""
        }
        if (userId.isBlank()) {
            // Si no hay userId, intentamos cargar la actividad primero
            viewModelScope.launch {
                cargarActividad()
                val userIdRecargado = _state.value.userId
                if (userIdRecargado.isNotBlank()) {
                    ejecutarPostulacion(userIdRecargado, vacanteId, mensajePresentacion)
                } else {
                    _state.update { it.copy(errorMessage = "Inicia sesión para postularte.") }
                }
            }
            return
        }
        viewModelScope.launch {
            ejecutarPostulacion(userId, vacanteId, mensajePresentacion)
        }
    }

    private suspend fun ejecutarPostulacion(userId: String, vacanteId: String, mensajePresentacion: String) {
        // Verificar si el candidato ya se postuló a esta vacante
        val yaPostulado = _state.value.postulaciones.any { it.vacanteId == vacanteId }
        if (yaPostulado) {
            _state.update { it.copy(errorMessage = "Ya te has postulado a esta oferta anteriormente.") }
            return
        }

        _state.update { it.copy(isPostulando = true, errorMessage = null) }

        val exito = postulacionRepository.crearPostulacion(
            candidatoId = userId,
            vacanteId = vacanteId,
            mensajePresentacion = mensajePresentacion
        )

        if (exito) {
            val nuevas = postulacionRepository.obtenerPostulacionesCandidato(userId)
            _state.update {
                it.copy(
                    isPostulando = false,
                    postulacionExitosa = true,
                    postulaciones = nuevas
                )
            }
        } else {
            _state.update {
                it.copy(isPostulando = false, errorMessage = "No se pudo enviar la postulación.")
            }
        }
    }

    // ── RECLUTADOR: ver postulantes de una oferta ────────────────────────

    // Expande una oferta para ver quiénes se postularon
    fun verPostulantesDeOferta(proyectoId: String, proyectoTitulo: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    ofertaExpandidaId = proyectoId,
                    ofertaExpandidaTitulo = proyectoTitulo,
                    isLoadingPostulaciones = true,
                    postulacionesOferta = emptyList()
                )
            }

            val postulaciones = postulacionRepository.obtenerPostulacionesVacante(proyectoId)

            _state.update {
                it.copy(
                    isLoadingPostulaciones = false,
                    postulacionesOferta = postulaciones
                )
            }
        }
    }

    // Cierra la vista de postulantes de una oferta
    fun cerrarPostulantes() {
        _state.update {
            it.copy(
                ofertaExpandidaId = null,
                ofertaExpandidaTitulo = "",
                postulacionesOferta = emptyList()
            )
        }
    }

    // ── RECLUTADOR: cambiar estado de una postulación ────────────────────

    fun actualizarEstadoPostulacion(postulacionId: String, nuevoEstado: String) {
        viewModelScope.launch {
            _state.update { it.copy(isActualizandoEstado = true, errorMessage = null) }

            val exito = postulacionRepository.actualizarEstado(postulacionId, nuevoEstado)

            if (exito) {
                // Actualizamos localmente la lista de postulantes de esa oferta
                val ofertaId = _state.value.ofertaExpandidaId
                val nuevasPostulaciones = if (ofertaId != null) {
                    postulacionRepository.obtenerPostulacionesVacante(ofertaId)
                } else _state.value.postulacionesOferta

                _state.update {
                    it.copy(
                        isActualizandoEstado = false,
                        estadoActualizadoExito = true,
                        postulacionesOferta = nuevasPostulaciones
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        isActualizandoEstado = false,
                        errorMessage = "No se pudo actualizar el estado."
                    )
                }
            }
        }
    }

    fun clearPostulacionExitosa() = _state.update { it.copy(postulacionExitosa = false) }
    fun clearEstadoActualizado() = _state.update { it.copy(estadoActualizadoExito = false) }
    fun clearError() = _state.update { it.copy(errorMessage = null) }
}

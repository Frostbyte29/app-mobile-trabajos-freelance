package com.example.proyecto_aplicaciones_moviles.presentation.screens.actividad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_aplicaciones_moviles.core.utils.SessionManager
import com.example.proyecto_aplicaciones_moviles.domain.repository.ChatRepository
import com.example.proyecto_aplicaciones_moviles.domain.repository.ContratoRepository
import com.example.proyecto_aplicaciones_moviles.domain.repository.PerfilRepository
import com.example.proyecto_aplicaciones_moviles.domain.repository.PostulacionRepository
import com.example.proyecto_aplicaciones_moviles.domain.repository.ValoracionRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MiActividadViewModel(
    private val postulacionRepository: PostulacionRepository,
    private val perfilRepository: PerfilRepository,
    private val chatRepository: ChatRepository,
    private val contratoRepository: ContratoRepository,
    private val valoracionRepository: ValoracionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MiActividadState())
    val state: StateFlow<MiActividadState> = _state.asStateFlow()

    init {
        cargarActividad()
    }

    fun cargarActividad() {
        val email = SessionManager.currentUserEmail ?: return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val usuario = perfilRepository.obtenerUsuarioPorCorreo(email)
            if (usuario == null) {
                _state.update { it.copy(isLoading = false, errorMessage = "No se pudo cargar tu actividad.") }
                return@launch
            }

            val roles = usuario.roles
            val userId = usuario.id

            if (userId.isNotBlank()) SessionManager.setUserId(userId)
            if (roles.contains("reclutador")) {
                usuario.empresaNombre.takeIf { it.isNotBlank() }
                    ?.let { SessionManager.setEmpresaNombre(it) }
            }
            val nombreCompleto = "${usuario.nombres} ${usuario.apellidos}".trim()
            if (nombreCompleto.isNotBlank()) SessionManager.setNombreCompleto(nombreCompleto)

            if (SessionManager.activeRole == null || !roles.contains(SessionManager.activeRole)) {
                val rolDefault = roles.firstOrNull() ?: "candidato"
                SessionManager.switchRole(rolDefault)
            }

            val tabInicial = when {
                SessionManager.activeRole != null && roles.contains(SessionManager.activeRole) ->
                    SessionManager.activeRole!!
                roles.isNotEmpty() -> roles.first()
                else -> "candidato"
            }

            val postulacionesDeferred = async {
                if (roles.contains("candidato") && userId.isNotBlank())
                    postulacionRepository.obtenerPostulacionesCandidato(userId)
                else emptyList()
            }
            val misProyectosDeferred = async {
                if (roles.contains("reclutador") && userId.isNotBlank())
                    postulacionRepository.obtenerProyectosReclutador(userId)
                else emptyList()
            }
            val contratosFreelancerDeferred = async {
                if (roles.contains("candidato") && userId.isNotBlank())
                    contratoRepository.obtenerContratosComoFreelancer(userId)
                else emptyList()
            }
            val contratosContratanteDeferred = async {
                if (roles.contains("reclutador") && userId.isNotBlank())
                    contratoRepository.obtenerContratosComoContratante(userId)
                else emptyList()
            }
            val misServiciosDeferred = async {
                if (roles.contains("candidato") && userId.isNotBlank())
                    postulacionRepository.obtenerServiciosCandidato(userId)
                else emptyList()
            }

            val postulaciones = postulacionesDeferred.await()
            val misProyectos = misProyectosDeferred.await()
            val contratosFreelancer = contratosFreelancerDeferred.await()
            val contratosContratante = contratosContratanteDeferred.await()
            val misServicios = misServiciosDeferred.await()

            val receptoresFinalizados = buildSet {
                contratosFreelancer.filter { it.estado == "finalizado" }.forEach { add(it.contratanteId) }
                contratosContratante.filter { it.estado == "finalizado" }.forEach { add(it.freelancerId) }
            }
            val valoracionesEmitidas = if (userId.isNotBlank() && receptoresFinalizados.isNotEmpty()) {
                receptoresFinalizados.map { receptorId ->
                    async {
                        try { valoracionRepository.obtenerValoracionesPorUsuario(receptorId) }
                        catch (e: Exception) { emptyList() }
                    }
                }.awaitAll().flatten().filter { it.usuarioEmisorId == userId }
            } else emptyList()

            _state.update {
                it.copy(
                    isLoading = false,
                    roles = roles,
                    userId = userId,
                    postulaciones = postulaciones,
                    misProyectos = misProyectos,
                    misServicios = misServicios,
                    tabActivo = tabInicial,
                    contratosFreelancer = contratosFreelancer,
                    contratosContratante = contratosContratante,
                    valoracionesEmitidas = valoracionesEmitidas
                )
            }
        }
    }

    fun setTabActivo(tab: String) {
        _state.update { it.copy(tabActivo = tab) }
    }

    fun postularse(
        vacanteId: String,
        mensajePresentacion: String,
        linkedinUrl: String? = null,
        repoUrl: String? = null
    ) {
        val userId = _state.value.userId.ifBlank {
            SessionManager.currentUserId ?: ""
        }
        if (userId.isBlank()) {
            viewModelScope.launch {
                cargarActividad()
                val userIdRecargado = _state.value.userId
                if (userIdRecargado.isNotBlank()) {
                    ejecutarPostulacion(userIdRecargado, vacanteId, mensajePresentacion, linkedinUrl, repoUrl)
                } else {
                    _state.update { it.copy(errorMessage = "Inicia sesión para postularte.") }
                }
            }
            return
        }
        viewModelScope.launch {
            ejecutarPostulacion(userId, vacanteId, mensajePresentacion, linkedinUrl, repoUrl)
        }
    }

    private suspend fun ejecutarPostulacion(
        userId: String,
        vacanteId: String,
        mensajePresentacion: String,
        linkedinUrl: String? = null,
        repoUrl: String? = null
    ) {
        val yaPostulado = _state.value.postulaciones.any { it.vacanteId == vacanteId }
        if (yaPostulado) {
            _state.update { it.copy(errorMessage = "Ya te has postulado a esta oferta anteriormente.") }
            return
        }

        _state.update { it.copy(isPostulando = true, errorMessage = null) }

        val exito = postulacionRepository.crearPostulacion(
            candidatoId = userId,
            vacanteId = vacanteId,
            mensajePresentacion = mensajePresentacion,
            linkedinUrl = linkedinUrl?.takeIf { it.isNotBlank() },
            repoUrl = repoUrl?.takeIf { it.isNotBlank() }
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

    fun cerrarPostulantes() {
        _state.update {
            it.copy(
                ofertaExpandidaId = null,
                ofertaExpandidaTitulo = "",
                postulacionesOferta = emptyList()
            )
        }
    }

    fun actualizarEstadoPostulacion(postulacionId: String, nuevoEstado: String) {
        viewModelScope.launch {
            _state.update { it.copy(isActualizandoEstado = true, errorMessage = null) }

            val exito = postulacionRepository.actualizarEstado(postulacionId, nuevoEstado)

            if (exito) {
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

    fun finalizarContrato(contratoId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isFinalizandoContrato = true, errorMessage = null) }
            val exito = contratoRepository.finalizarContrato(contratoId)
            if (exito) {
                val userId = _state.value.userId
                val contratosContratante = contratoRepository.obtenerContratosComoContratante(userId)
                _state.update {
                    it.copy(
                        isFinalizandoContrato = false,
                        contratoFinalizadoExito = true,
                        contratosContratante = contratosContratante
                    )
                }
            } else {
                _state.update { it.copy(isFinalizandoContrato = false, errorMessage = "No se pudo finalizar el contrato.") }
            }
        }
    }

    fun clearContratoFinalizadoExito() = _state.update { it.copy(contratoFinalizadoExito = false) }

    fun setFiltroContratos(esFreelancer: Boolean, filtro: String) {
        _state.update {
            if (esFreelancer) it.copy(filtroContratosFreelancer = filtro)
            else it.copy(filtroContratosContratante = filtro)
        }
    }

    fun abrirFormValoracion(contratoId: String, receptorId: String, tituloContrato: String, ofertaId: String) {
        val existente = _state.value.valoracionesEmitidas.firstOrNull { v ->
            v.usuarioReceptorId == receptorId && v.proyectoId == ofertaId
        }
        _state.update {
            it.copy(
                contratoAValorarId = contratoId,
                receptorIdAValorar = receptorId,
                tituloContratoAValorar = tituloContrato,
                ofertaIdAValorar = ofertaId,
                valoracionIdAEditar = existente?.id,
                mostrarFormValoracion = true,
                puntuacionSeleccionada = existente?.puntuacion ?: 5,
                comentarioValoracion = existente?.comentario ?: "",
                errorMessage = null
            )
        }
    }

    fun cerrarFormValoracion() =
        _state.update {
            it.copy(
                mostrarFormValoracion = false,
                contratoAValorarId = null,
                receptorIdAValorar = null,
                valoracionIdAEditar = null
            )
        }

    fun onPuntuacionChange(p: Int) = _state.update { it.copy(puntuacionSeleccionada = p) }
    fun onComentarioValoracionChange(v: String) = _state.update { it.copy(comentarioValoracion = v, errorMessage = null) }

    fun enviarValoracion() {
        val s = _state.value
        val emisorId = SessionManager.currentUserId ?: return
        val receptorId = s.receptorIdAValorar ?: return

        if (s.comentarioValoracion.isBlank()) {
            _state.update { it.copy(errorMessage = "Escribí un comentario antes de enviar.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isEnviandoValoracion = true, errorMessage = null) }

            val exito = if (s.valoracionIdAEditar != null) {
                valoracionRepository.actualizarValoracion(
                    valoracionId = s.valoracionIdAEditar,
                    puntuacion = s.puntuacionSeleccionada,
                    comentario = s.comentarioValoracion
                )
            } else {
                valoracionRepository.crearValoracion(
                    emisorId = emisorId,
                    receptorId = receptorId,
                    proyectoId = s.ofertaIdAValorar,
                    puntuacion = s.puntuacionSeleccionada,
                    comentario = s.comentarioValoracion
                )
            }

            if (exito) {
                val nuevasDelReceptor = try {
                    valoracionRepository.obtenerValoracionesPorUsuario(receptorId)
                        .filter { it.usuarioEmisorId == emisorId }
                } catch (e: Exception) { emptyList() }

                _state.update { s ->
                    val sinReceptorAnterior = s.valoracionesEmitidas
                        .filter { it.usuarioReceptorId != receptorId }
                    s.copy(
                        isEnviandoValoracion = false,
                        mostrarFormValoracion = false,
                        contratoAValorarId = null,
                        receptorIdAValorar = null,
                        valoracionIdAEditar = null,
                        valoracionEnviadaExito = true,
                        valoracionesEmitidas = sinReceptorAnterior + nuevasDelReceptor
                    )
                }
            } else {
                _state.update { it.copy(isEnviandoValoracion = false, errorMessage = "No se pudo enviar la valoración.") }
            }
        }
    }

    fun clearValoracionExito() = _state.update { it.copy(valoracionEnviadaExito = false) }

    fun iniciarChatConCandidato(candidatoId: String, nombreCandidato: String) {
        val usuarioId = SessionManager.currentUserId ?: return
        viewModelScope.launch {
            _state.update { it.copy(isBuscandoChat = true, errorMessage = null) }
            try {
                val conv = chatRepository.obtenerOCrearConversacion(usuarioId, candidatoId)
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
package com.example.proyecto_aplicaciones_moviles.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_aplicaciones_moviles.core.utils.SessionManager
import com.example.proyecto_aplicaciones_moviles.data.remote.EmpresaInfoDto
import com.example.proyecto_aplicaciones_moviles.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: ProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        cargarPerfilUsuario()
    }

    fun cargarPerfilUsuario() {
        val email = SessionManager.currentUserEmail
        if (email == null) {
            _state.update { it.copy(isLoading = false) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val usuario = repository.obtenerUsuarioPorCorreo(email)

            if (usuario != null) {
                val rolInicial = SessionManager.activeRole ?: usuario.roles?.firstOrNull()
                rolInicial?.let { SessionManager.switchRole(it) }

                val emp = usuario.empresaInfo

                // Guardamos datos en SessionManager para uso global (PublishScreen, filtros)
                usuario.id?.let { SessionManager.setUserId(it) }
                // Solo guardamos empresaNombre si tiene rol reclutador
                if (usuario.roles?.contains("reclutador") == true) {
                    emp?.nombre?.takeIf { it.isNotBlank() }?.let { SessionManager.setEmpresaNombre(it) }
                }
                val nombreCompleto = "${usuario.nombres ?: ""} ${usuario.apellidos ?: ""}".trim()
                if (nombreCompleto.isNotBlank()) SessionManager.setNombreCompleto(nombreCompleto)

                _state.update {
                    it.copy(
                        isLoading = false,
                        userId = usuario.id,
                        nombres = usuario.nombres ?: "",
                        apellidos = usuario.apellidos ?: "",
                        correo = usuario.correo ?: "",
                        telefono = usuario.telefono ?: "",
                        fotoPerfilUrl = usuario.fotoPerfilUrl,
                        roles = usuario.roles ?: emptyList(),
                        activeRole = rolInicial,
                        empresaNombre = emp?.nombre ?: "",
                        empresaRubro = emp?.rubro ?: "",
                        empresaCorreoContacto = emp?.correoContacto ?: "",
                        empresaTelefono = emp?.telefono ?: "",
                        empresaSitioWeb = emp?.sitioWeb ?: "",
                        empresaDireccion = emp?.direccion ?: ""
                    )
                }
            } else {
                _state.update {
                    it.copy(isLoading = false, errorMessage = "No se pudo cargar el perfil. Intenta de nuevo.")
                }
            }
        }
    }

    // Cambia el rol activo sin llamar a AWS
    fun seleccionarRol(role: String) {
        SessionManager.switchRole(role)
        _state.update { it.copy(activeRole = role) }
    }

    // Agrega el segundo rol en AWS
    fun agregarSegundoRol() {
        val s = _state.value
        val userId = s.userId ?: return

        val rolFaltante = when {
            s.roles.contains("candidato") && !s.roles.contains("reclutador") -> "reclutador"
            s.roles.contains("reclutador") && !s.roles.contains("candidato") -> "candidato"
            else -> return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSavingRole = true, errorMessage = null) }
            val exito = repository.agregarRolAUsuario(userId, s.roles, rolFaltante)
            if (exito) {
                _state.update {
                    it.copy(
                        isSavingRole = false,
                        roles = (it.roles + rolFaltante).distinct(),
                        roleAddedSuccess = true
                    )
                }
            } else {
                _state.update { it.copy(isSavingRole = false, errorMessage = "No se pudo agregar el perfil.") }
            }
        }
    }

    // ── EDITAR DATOS PERSONALES ────────────────────────────────────────────

    fun iniciarEdicionPersonal() {
        val s = _state.value
        _state.update {
            it.copy(
                editMode = "personal",
                editNombres = s.nombres,
                editApellidos = s.apellidos,
                editTelefono = s.telefono,
                errorMessage = null
            )
        }
    }

    fun onEditNombresChange(v: String) = _state.update { it.copy(editNombres = v, errorMessage = null) }
    fun onEditApellidosChange(v: String) = _state.update { it.copy(editApellidos = v, errorMessage = null) }
    fun onEditTelefonoChange(v: String) = _state.update { it.copy(editTelefono = v, errorMessage = null) }

    fun guardarDatosPersonales() {
        val s = _state.value
        val userId = s.userId ?: return

        if (s.editNombres.isBlank()) {
            _state.update { it.copy(errorMessage = "El nombre no puede estar vacío.") }
            return
        }
        if (s.editApellidos.isBlank()) {
            _state.update { it.copy(errorMessage = "Los apellidos no pueden estar vacíos.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSavingEdit = true, errorMessage = null) }

            // Conservamos los datos de empresa si ya existen
            val empInfo = construirEmpresaInfoONulo(s)

            val exito = repository.actualizarDatosUsuario(
                userId = userId,
                nombres = s.editNombres.trim(),
                apellidos = s.editApellidos.trim(),
                telefono = s.editTelefono.trim(),
                roles = s.roles,
                empresaInfo = empInfo
            )

            if (exito) {
                _state.update {
                    it.copy(
                        isSavingEdit = false,
                        editMode = "none",
                        nombres = it.editNombres.trim(),
                        apellidos = it.editApellidos.trim(),
                        telefono = it.editTelefono.trim(),
                        editSuccess = true
                    )
                }
            } else {
                _state.update { it.copy(isSavingEdit = false, errorMessage = "No se pudieron guardar los cambios.") }
            }
        }
    }

    // ── EDITAR DATOS DE EMPRESA (solo reclutadores) ────────────────────────

    fun iniciarEdicionEmpresa() {
        val s = _state.value
        _state.update {
            it.copy(
                editMode = "empresa",
                editEmpresaNombre = s.empresaNombre,
                editEmpresaRubro = s.empresaRubro,
                editEmpresaCorreoContacto = s.empresaCorreoContacto,
                editEmpresaTelefono = s.empresaTelefono,
                editEmpresaSitioWeb = s.empresaSitioWeb,
                editEmpresaDireccion = s.empresaDireccion,
                errorMessage = null
            )
        }
    }

    fun onEditEmpresaNombreChange(v: String) = _state.update { it.copy(editEmpresaNombre = v, errorMessage = null) }
    fun onEditEmpresaRubroChange(v: String) = _state.update { it.copy(editEmpresaRubro = v, errorMessage = null) }
    fun onEditEmpresaCorreoChange(v: String) = _state.update { it.copy(editEmpresaCorreoContacto = v, errorMessage = null) }
    fun onEditEmpresaTelefonoChange(v: String) = _state.update { it.copy(editEmpresaTelefono = v, errorMessage = null) }
    fun onEditEmpresaSitioWebChange(v: String) = _state.update { it.copy(editEmpresaSitioWeb = v, errorMessage = null) }
    fun onEditEmpresaDireccionChange(v: String) = _state.update { it.copy(editEmpresaDireccion = v, errorMessage = null) }

    fun guardarDatosEmpresa() {
        val s = _state.value
        val userId = s.userId ?: return

        if (s.editEmpresaNombre.isBlank()) {
            _state.update { it.copy(errorMessage = "El nombre de la empresa no puede estar vacío.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSavingEdit = true, errorMessage = null) }

            val empInfo = EmpresaInfoDto(
                nombre = s.editEmpresaNombre.trim(),
                rubro = s.editEmpresaRubro.trim().ifBlank { null },
                correoContacto = s.editEmpresaCorreoContacto.trim().ifBlank { null },
                telefono = s.editEmpresaTelefono.trim().ifBlank { null },
                sitioWeb = s.editEmpresaSitioWeb.trim().ifBlank { null },
                direccion = s.editEmpresaDireccion.trim().ifBlank { null }
            )

            val exito = repository.actualizarDatosUsuario(
                userId = userId,
                nombres = s.nombres,
                apellidos = s.apellidos,
                telefono = s.telefono,
                roles = s.roles,
                empresaInfo = empInfo
            )

            if (exito) {
                _state.update {
                    it.copy(
                        isSavingEdit = false,
                        editMode = "none",
                        empresaNombre = it.editEmpresaNombre.trim(),
                        empresaRubro = it.editEmpresaRubro.trim(),
                        empresaCorreoContacto = it.editEmpresaCorreoContacto.trim(),
                        empresaTelefono = it.editEmpresaTelefono.trim(),
                        empresaSitioWeb = it.editEmpresaSitioWeb.trim(),
                        empresaDireccion = it.editEmpresaDireccion.trim(),
                        editSuccess = true
                    )
                }
            } else {
                _state.update { it.copy(isSavingEdit = false, errorMessage = "No se pudieron guardar los datos de la empresa.") }
            }
        }
    }

    fun cancelarEdicion() {
        _state.update { it.copy(editMode = "none", errorMessage = null) }
    }

    // Logout: limpia la sesión y resetea el estado
    fun logout() {
        SessionManager.logout()
        _state.value = ProfileState()
    }

    fun limpiarMensajeExito() = _state.update { it.copy(roleAddedSuccess = false) }
    fun limpiarExitoEdicion() = _state.update { it.copy(editSuccess = false) }
    fun clearError() = _state.update { it.copy(errorMessage = null) }

    // Construye EmpresaInfoDto solo si el usuario ya tiene datos de empresa guardados
    private fun construirEmpresaInfoONulo(s: ProfileState): EmpresaInfoDto? {
        val tieneEmpresa = s.empresaNombre.isNotBlank() || s.empresaRubro.isNotBlank()
        if (!tieneEmpresa) return null
        return EmpresaInfoDto(
            nombre = s.empresaNombre.ifBlank { null },
            rubro = s.empresaRubro.ifBlank { null },
            correoContacto = s.empresaCorreoContacto.ifBlank { null },
            telefono = s.empresaTelefono.ifBlank { null },
            sitioWeb = s.empresaSitioWeb.ifBlank { null },
            direccion = s.empresaDireccion.ifBlank { null }
        )
    }
}

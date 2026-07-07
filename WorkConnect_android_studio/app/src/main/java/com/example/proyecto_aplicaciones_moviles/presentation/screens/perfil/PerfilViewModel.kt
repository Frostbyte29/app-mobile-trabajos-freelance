package com.example.proyecto_aplicaciones_moviles.presentation.screens.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_aplicaciones_moviles.core.utils.SessionManager
import com.example.proyecto_aplicaciones_moviles.domain.model.EmpresaInfo
import com.example.proyecto_aplicaciones_moviles.domain.repository.PerfilRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PerfilViewModel(
    private val repository: PerfilRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PerfilState())
    val state: StateFlow<PerfilState> = _state.asStateFlow()

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
                val rolInicial = SessionManager.activeRole ?: usuario.roles.firstOrNull()
                rolInicial?.let { SessionManager.switchRole(it) }

                SessionManager.setUserId(usuario.id)
                if (usuario.roles.contains("reclutador")) {
                    usuario.empresaNombre.takeIf { it.isNotBlank() }?.let { SessionManager.setEmpresaNombre(it) }
                }
                val nombreCompleto = "${usuario.nombres} ${usuario.apellidos}".trim()
                if (nombreCompleto.isNotBlank()) SessionManager.setNombreCompleto(nombreCompleto)

                _state.update {
                    it.copy(
                        isLoading = false,
                        userId = usuario.id,
                        nombres = usuario.nombres,
                        apellidos = usuario.apellidos,
                        correo = usuario.correo,
                        telefono = usuario.telefono,
                        fotoPerfilUrl = usuario.fotoPerfilUrl,
                        acercaDe = usuario.acercaDe,
                        roles = usuario.roles,
                        activeRole = rolInicial,
                        empresaNombre = usuario.empresaNombre,
                        empresaRubro = usuario.empresaRubro,
                        empresaCorreoContacto = usuario.empresaCorreoContacto,
                        empresaTelefono = usuario.empresaTelefono,
                        empresaSitioWeb = usuario.empresaSitioWeb,
                        empresaDireccion = usuario.empresaDireccion
                    )
                }
            } else {
                _state.update {
                    it.copy(isLoading = false, errorMessage = "No se pudo cargar el perfil. Intenta de nuevo.")
                }
            }
        }
    }

    fun seleccionarRol(role: String) {
        SessionManager.switchRole(role)
        _state.update { it.copy(activeRole = role) }
    }

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

    fun iniciarEdicionPersonal() {
        val s = _state.value
        val telefonoTrimmed = s.telefono.trim()
        val regex = Regex("^\\+(\\d{1,2})(\\d.*)$")
        val match = regex.find(telefonoTrimmed)
        val codigoPais = match?.groupValues?.getOrNull(1) ?: "51"
        val numero = match?.groupValues?.getOrNull(2) ?: ""
        _state.update {
            it.copy(
                editMode = "personal",
                editNombres = s.nombres,
                editApellidos = s.apellidos,
                editCodigoPais = codigoPais,
                editNumeroTelefono = numero,
                editAcercaDe = s.acercaDe,
                errorMessage = null
            )
        }
    }

    fun onEditNombresChange(v: String) = _state.update { it.copy(editNombres = v, errorMessage = null) }
    fun onEditApellidosChange(v: String) = _state.update { it.copy(editApellidos = v, errorMessage = null) }
    fun onEditCodigoPaisChange(v: String) = _state.update { it.copy(editCodigoPais = v, errorMessage = null) }
    fun onEditNumeroTelefonoChange(v: String) = _state.update { it.copy(editNumeroTelefono = v, errorMessage = null) }
    fun onEditAcercaDeChange(v: String) = _state.update { it.copy(editAcercaDe = v, errorMessage = null) }

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

            val empInfo = construirEmpresaInfoONulo(s)

            val exito = repository.actualizarDatosUsuario(
                userId = userId,
                nombres = s.editNombres.trim(),
                apellidos = s.editApellidos.trim(),
                telefono = "+${s.editCodigoPais.trim()}${s.editNumeroTelefono.trim()}",
                roles = s.roles,
                acercaDe = s.editAcercaDe.trim().ifBlank { null },
                empresaInfo = empInfo
            )

            if (exito) {
                val nombresNuevo = s.editNombres.trim()
                val apellidosNuevo = s.editApellidos.trim()
                SessionManager.setNombreCompleto("$nombresNuevo $apellidosNuevo".trim())
                _state.update {
                    it.copy(
                        isSavingEdit = false,
                        editMode = "none",
                        nombres = nombresNuevo,
                        apellidos = apellidosNuevo,
                        telefono = "+${it.editCodigoPais.trim()}${it.editNumeroTelefono.trim()}",
                        acercaDe = it.editAcercaDe.trim(),
                        editSuccess = true
                    )
                }
            } else {
                _state.update { it.copy(isSavingEdit = false, errorMessage = "No se pudieron guardar los cambios.") }
            }
        }
    }

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

            val empInfo = EmpresaInfo(
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

    fun logout() {
        SessionManager.logout()
        _state.value = PerfilState()
    }

    fun limpiarMensajeExito() = _state.update { it.copy(roleAddedSuccess = false) }
    fun limpiarExitoEdicion() = _state.update { it.copy(editSuccess = false) }
    fun clearError() = _state.update { it.copy(errorMessage = null) }

    private fun construirEmpresaInfoONulo(s: PerfilState): EmpresaInfo? {
        val tieneEmpresa = s.empresaNombre.isNotBlank() || s.empresaRubro.isNotBlank()
        if (!tieneEmpresa) return null
        return EmpresaInfo(
            nombre = s.empresaNombre.ifBlank { null },
            rubro = s.empresaRubro.ifBlank { null },
            correoContacto = s.empresaCorreoContacto.ifBlank { null },
            telefono = s.empresaTelefono.ifBlank { null },
            sitioWeb = s.empresaSitioWeb.ifBlank { null },
            direccion = s.empresaDireccion.ifBlank { null }
        )
    }
}
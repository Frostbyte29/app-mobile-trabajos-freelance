package com.example.proyecto_aplicaciones_moviles.presentation.screens.profile

data class ProfileState(

    val isLoading: Boolean = false,
    val isSavingRole: Boolean = false,
    val isSavingEdit: Boolean = false,

    // Datos personales del usuario
    val userId: String? = null,
    val nombres: String = "",
    val apellidos: String = "",
    val correo: String = "",
    val telefono: String = "",
    val fotoPerfilUrl: String? = null,
    val roles: List<String> = emptyList(),

    // Rol activo seleccionado ("candidato" o "reclutador")
    val activeRole: String? = null,

    // Datos de empresa (solo aplican si tiene rol reclutador)
    val empresaNombre: String = "",
    val empresaRubro: String = "",
    val empresaCorreoContacto: String = "",
    val empresaTelefono: String = "",
    val empresaSitioWeb: String = "",
    val empresaDireccion: String = "",

    // Controla qué formulario de edición está visible
    // "none" = ninguno, "personal" = datos personales, "empresa" = datos de empresa
    val editMode: String = "none",

    // Campos temporales del formulario de datos personales
    val editNombres: String = "",
    val editApellidos: String = "",
    val editTelefono: String = "",

    // Campos temporales del formulario de empresa
    val editEmpresaNombre: String = "",
    val editEmpresaRubro: String = "",
    val editEmpresaCorreoContacto: String = "",
    val editEmpresaTelefono: String = "",
    val editEmpresaSitioWeb: String = "",
    val editEmpresaDireccion: String = "",

    val errorMessage: String? = null,
    val roleAddedSuccess: Boolean = false,
    val editSuccess: Boolean = false
)

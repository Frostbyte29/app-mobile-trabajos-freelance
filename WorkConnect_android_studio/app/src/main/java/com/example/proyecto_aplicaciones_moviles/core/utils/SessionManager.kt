package com.example.proyecto_aplicaciones_moviles.core.utils

object SessionManager {

    // Si el correo es null, sabemos que es un invitado
    var currentUserEmail: String? = null

    // ID real del usuario en DynamoDB
    var currentUserId: String? = null

    // Nombre de empresa del reclutador
    var currentEmpresaNombre: String? = null

    // Nombre completo del usuario (para mostrar en ofertas de servicio)
    var currentNombreCompleto: String? = null

    // Rol activo seleccionado por el usuario ("candidato" o "reclutador")
    var activeRole: String? = null

    val isGuest: Boolean
        get() = currentUserEmail == null

    fun login(email: String) {
        currentUserEmail = email
    }

    fun setUserId(id: String) {
        currentUserId = id
    }

    fun setEmpresaNombre(nombre: String) {
        currentEmpresaNombre = nombre
    }

    fun setNombreCompleto(nombre: String) {
        currentNombreCompleto = nombre
    }

    fun switchRole(role: String) {
        activeRole = role
    }

    fun logout() {
        currentUserEmail = null
        currentUserId = null
        currentEmpresaNombre = null
        currentNombreCompleto = null
        activeRole = null
    }
}

package com.example.proyecto_aplicaciones_moviles.core.utils

object SessionManager {

    var currentUserEmail: String? = null

    var currentUserId: String? = null

    var currentEmpresaNombre: String? = null

    var currentNombreCompleto: String? = null

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
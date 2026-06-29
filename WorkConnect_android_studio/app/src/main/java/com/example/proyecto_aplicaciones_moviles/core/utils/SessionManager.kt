package com.example.proyecto_aplicaciones_moviles.core.utils

object SessionManager {
    // Si el correo es null, sabemos que es un invitado
    var currentUserEmail: String? = null

    // Variable rápida para preguntar si es invitado
    val isGuest: Boolean
        get() = currentUserEmail == null

    fun login(email: String) {
        currentUserEmail = email
    }

    fun logout() {
        currentUserEmail = null
    }
}
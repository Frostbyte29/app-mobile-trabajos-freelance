package com.example.proyecto_aplicaciones_moviles.core.utils

object SessionManager {
    // Si el correo es null, sabemos que es un invitado
    var correoActualUsuario: String? = null

    // Variable rápida para preguntar si es invitado
    val isGuest: Boolean
        get() = correoActualUsuario == null

    fun login(email: String) {
        correoActualUsuario = email
    }

    fun logout() {
        correoActualUsuario = null
    }
}
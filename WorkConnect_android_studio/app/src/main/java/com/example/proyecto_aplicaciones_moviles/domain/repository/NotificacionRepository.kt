package com.example.proyecto_aplicaciones_moviles.domain.repository

import com.example.proyecto_aplicaciones_moviles.domain.model.Notificacion

interface NotificacionRepository {
    suspend fun obtenerNotificaciones(usuarioId: String): List<Notificacion>
    suspend fun marcarLeida(notificacionId: String): Boolean
}

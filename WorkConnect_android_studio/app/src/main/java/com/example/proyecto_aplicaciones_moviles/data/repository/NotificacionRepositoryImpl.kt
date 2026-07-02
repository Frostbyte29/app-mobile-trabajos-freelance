package com.example.proyecto_aplicaciones_moviles.data.repository

import android.util.Log
import com.example.proyecto_aplicaciones_moviles.data.remote.WorkConnectApi
import com.example.proyecto_aplicaciones_moviles.domain.model.Notificacion
import com.example.proyecto_aplicaciones_moviles.domain.repository.NotificacionRepository

class NotificacionRepositoryImpl(
    private val api: WorkConnectApi
) : NotificacionRepository {

    override suspend fun obtenerNotificaciones(usuarioId: String): List<Notificacion> {
        return try {
            val response = api.getNotificaciones(usuarioId)
            if (!response.isSuccessful) return emptyList()
            response.body()?.data?.items?.map { dto ->
                Notificacion(
                    id            = dto.id ?: "",
                    usuarioId     = dto.usuarioId ?: "",
                    titulo        = dto.titulo ?: "",
                    mensaje       = dto.mensaje ?: "",
                    tipo          = dto.tipo ?: "",
                    referenciaId  = dto.referenciaId,
                    leida         = dto.leida ?: false,
                    fechaCreacion = dto.fechaCreacion ?: ""
                )
            } ?: emptyList()
        } catch (e: Exception) {
            Log.e("NOTIF_REPO", "Error al cargar notificaciones: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun marcarLeida(notificacionId: String): Boolean {
        return try {
            api.marcarNotificacionLeida(notificacionId).isSuccessful
        } catch (e: Exception) {
            Log.e("NOTIF_REPO", "Error al marcar leída: ${e.message}", e)
            false
        }
    }
}

package com.example.proyecto_aplicaciones_moviles.data.repository

import android.util.Log
import com.example.proyecto_aplicaciones_moviles.data.remote.ConversacionRequestDto
import com.example.proyecto_aplicaciones_moviles.data.remote.MensajeRequestDto
import com.example.proyecto_aplicaciones_moviles.data.remote.WorkConnectApi
import com.example.proyecto_aplicaciones_moviles.domain.model.Conversacion
import com.example.proyecto_aplicaciones_moviles.domain.model.Mensaje
import com.example.proyecto_aplicaciones_moviles.domain.repository.ChatRepository

class ChatRepositoryImpl(
    private val api: WorkConnectApi
) : ChatRepository {

    override suspend fun obtenerConversaciones(usuarioId: String): List<Conversacion> {
        return try {
            val response = api.getConversaciones(usuarioId)
            if (!response.isSuccessful) {
                Log.e("CHAT_REPO", "Error al obtener conversaciones: ${response.code()}")
                return emptyList()
            }

            val dtos = response.body()?.data?.items ?: return emptyList()

            val dtosValidos = dtos.filter {
                it.id != null && it.participanteAId != null && it.participanteBId != null
            }

            val otrosIds = dtosValidos
                .map { if (it.participanteAId == usuarioId) it.participanteBId!! else it.participanteAId!! }
                .distinct()

            val nombresPorId = otrosIds.associate { id ->
                val nombre = try {
                    val r = api.getUserById(id)
                    if (r.isSuccessful) {
                        val u = r.body()?.data
                        "${u?.nombres ?: ""} ${u?.apellidos ?: ""}".trim().ifBlank { "Usuario" }
                    } else "Usuario"
                } catch (e: Exception) {
                    Log.e("CHAT_REPO", "Error al resolver nombre de $id: ${e.message}")
                    "Usuario"
                }
                id to nombre
            }

            dtosValidos.map { dto ->
                val otroId = if (dto.participanteAId == usuarioId) dto.participanteBId!! else dto.participanteAId!!
                Conversacion(
                    id = dto.id!!,
                    otroParticipanteId = otroId,
                    nombreOtroParticipante = nombresPorId[otroId] ?: "Usuario",
                    vacanteId = dto.vacanteId,
                    activa = dto.activa ?: true,
                    fechaCreacion = dto.fechaCreacion ?: ""
                )
            }
        } catch (e: Exception) {
            Log.e("CHAT_REPO", "Excepción al obtener conversaciones: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun obtenerMensajes(conversacionId: String, usuarioId: String): List<Mensaje> {
        return try {
            val response = api.getMensajes(conversacionId)
            if (!response.isSuccessful) {
                Log.e("CHAT_REPO", "Error al obtener mensajes: ${response.code()}")
                return emptyList()
            }

            val dtos = response.body()?.data?.items ?: return emptyList()

            dtos.mapNotNull { dto ->
                if (dto.id == null || dto.emisorId == null || dto.contenido == null) null
                else Mensaje(
                    id = dto.id,
                    emisorId = dto.emisorId,
                    contenido = dto.contenido,
                    fechaEnvio = dto.fechaEnvio ?: "",
                    esMio = dto.emisorId == usuarioId,
                    leido = dto.leido ?: false
                )
            }
        } catch (e: Exception) {
            Log.e("CHAT_REPO", "Excepción al obtener mensajes: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun enviarMensaje(conversacionId: String, emisorId: String, contenido: String): Boolean {
        return try {
            val response = api.enviarMensaje(
                conversacionId,
                MensajeRequestDto(emisorId = emisorId, contenido = contenido)
            )
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("CHAT_REPO", "Error al enviar mensaje: ${e.message}", e)
            false
        }
    }

    override suspend fun obtenerOCrearConversacion(
        usuarioId: String,
        otroUsuarioId: String,
        vacanteId: String?
    ): Conversacion? {
        return try {
            // Buscar conversación existente entre estos dos usuarios
            val existente = obtenerConversaciones(usuarioId).find { conv ->
                conv.otroParticipanteId == otroUsuarioId
            }
            if (existente != null) return existente

            // Si no existe creamos una nueva
            val response = api.crearConversacion(
                ConversacionRequestDto(
                    participanteAId = usuarioId,
                    participanteBId = otroUsuarioId,
                    vacanteId = vacanteId
                )
            )
            if (!response.isSuccessful) {
                Log.e("CHAT_REPO", "Error al crear conversación: ${response.code()}")
                return null
            }

            val dto = response.body()?.data ?: return null
            if (dto.id == null) return null

            val nombreOtro = try {
                val r = api.getUserById(otroUsuarioId)
                if (r.isSuccessful) {
                    val u = r.body()?.data
                    "${u?.nombres ?: ""} ${u?.apellidos ?: ""}".trim().ifBlank { "Usuario" }
                } else "Usuario"
            } catch (e: Exception) {
                Log.e("CHAT_REPO", "Error al resolver nombre de $otroUsuarioId: ${e.message}")
                "Usuario"
            }

            Conversacion(
                id = dto.id,
                otroParticipanteId = otroUsuarioId,
                nombreOtroParticipante = nombreOtro,
                vacanteId = dto.vacanteId,
                activa = dto.activa ?: true,
                fechaCreacion = dto.fechaCreacion ?: ""
            )
        } catch (e: Exception) {
            Log.e("CHAT_REPO", "Excepción en obtenerOCrear: ${e.message}", e)
            null
        }
    }
}

package com.example.proyecto_aplicaciones_moviles.domain.repository

import com.example.proyecto_aplicaciones_moviles.domain.model.Conversacion
import com.example.proyecto_aplicaciones_moviles.domain.model.Mensaje

interface ChatRepository {
    suspend fun obtenerConversaciones(usuarioId: String): List<Conversacion>
    suspend fun obtenerMensajes(conversacionId: String, usuarioId: String): List<Mensaje>
    suspend fun enviarMensaje(conversacionId: String, emisorId: String, contenido: String): Boolean
    suspend fun obtenerOCrearConversacion(usuarioId: String, otroUsuarioId: String, vacanteId: String? = null): Conversacion?
}

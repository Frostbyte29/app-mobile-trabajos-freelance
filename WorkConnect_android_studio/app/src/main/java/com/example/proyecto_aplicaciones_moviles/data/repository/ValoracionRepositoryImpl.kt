package com.example.proyecto_aplicaciones_moviles.data.repository

import android.util.Log
import com.example.proyecto_aplicaciones_moviles.data.remote.ValoracionRequestDto
import com.example.proyecto_aplicaciones_moviles.data.remote.ValoracionUpdateDto
import com.example.proyecto_aplicaciones_moviles.data.remote.WorkConnectApi
import com.example.proyecto_aplicaciones_moviles.domain.model.Valoracion
import com.example.proyecto_aplicaciones_moviles.domain.repository.ValoracionRepository

class ValoracionRepositoryImpl(
    private val api: WorkConnectApi
) : ValoracionRepository {

    override suspend fun obtenerValoracionesPorUsuario(usuarioReceptorId: String): List<Valoracion> {
        return try {
            val response = api.getValoraciones(usuarioReceptorId)
            if (!response.isSuccessful) return emptyList()

            val dtos = response.body()?.data?.items ?: return emptyList()

            val usuarios = try {
                val r = api.getUsers()
                if (r.isSuccessful) r.body()?.data?.items?.associateBy { it.id } ?: emptyMap()
                else emptyMap()
            } catch (e: Exception) { emptyMap() }

            dtos.map { dto ->
                val emisorId = dto.usuarioEmisorId ?: ""
                val usuario = usuarios[emisorId]
                val nombreEmisor = when {
                    usuario != null -> "${usuario.nombres ?: ""} ${usuario.apellidos ?: ""}".trim()
                        .ifBlank { "Anónimo" }
                    else -> "Anónimo"
                }
                Valoracion(
                    id = dto.id ?: "",
                    usuarioEmisorId = emisorId,
                    usuarioReceptorId = dto.usuarioReceptorId ?: "",
                    proyectoId = dto.proyectoId,
                    puntuacion = dto.puntuacion ?: 0,
                    comentario = dto.comentario ?: "",
                    fechaCreacion = dto.fechaCreacion ?: "",
                    editada = dto.editada ?: false,
                    nombreEmisor = nombreEmisor
                )
            }
        } catch (e: Exception) {
            Log.e("VALORACION_REPO", "Error: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun crearValoracion(
        emisorId: String,
        receptorId: String,
        proyectoId: String?,
        puntuacion: Int,
        comentario: String
    ): Boolean {
        return try {
            val response = api.crearValoracion(
                ValoracionRequestDto(
                    usuarioEmisorId = emisorId,
                    usuarioReceptorId = receptorId,
                    proyectoId = proyectoId,
                    puntuacion = puntuacion,
                    comentario = comentario.ifBlank { null }
                )
            )
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("VALORACION_REPO", "Error al crear: ${e.message}", e)
            false
        }
    }

    override suspend fun actualizarValoracion(valoracionId: String, puntuacion: Int, comentario: String): Boolean {
        return try {
            val response = api.actualizarValoracion(
                id = valoracionId,
                request = ValoracionUpdateDto(
                    puntuacion = puntuacion,
                    comentario = comentario.ifBlank { null }
                )
            )
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("VALORACION_REPO", "Error al actualizar: ${e.message}", e)
            false
        }
    }
}
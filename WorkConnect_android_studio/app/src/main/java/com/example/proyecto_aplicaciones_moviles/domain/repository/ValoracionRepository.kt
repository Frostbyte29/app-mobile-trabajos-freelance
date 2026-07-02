package com.example.proyecto_aplicaciones_moviles.domain.repository

import com.example.proyecto_aplicaciones_moviles.domain.model.Valoracion

interface ValoracionRepository {
    suspend fun obtenerValoracionesPorUsuario(usuarioReceptorId: String): List<Valoracion>
    suspend fun crearValoracion(
        emisorId: String,
        receptorId: String,
        proyectoId: String?,
        puntuacion: Int,
        comentario: String
    ): Boolean
}

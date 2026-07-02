package com.example.proyecto_aplicaciones_moviles.domain.repository

import com.example.proyecto_aplicaciones_moviles.domain.model.Postulacion
import com.example.proyecto_aplicaciones_moviles.domain.model.Project

interface PostulacionRepository {
    suspend fun crearPostulacion(
        candidatoId: String,
        vacanteId: String,
        mensajePresentacion: String
    ): Boolean
    suspend fun obtenerPostulacionesCandidato(candidatoId: String): List<Postulacion>
    suspend fun obtenerPostulacionesVacante(vacanteId: String): List<Postulacion>
    suspend fun actualizarEstado(postulacionId: String, nuevoEstado: String): Boolean
    suspend fun obtenerProyectosReclutador(userId: String): List<Project>
}

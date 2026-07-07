package com.example.proyecto_aplicaciones_moviles.domain.repository

import com.example.proyecto_aplicaciones_moviles.domain.model.Postulacion
import com.example.proyecto_aplicaciones_moviles.domain.model.Proyecto

interface PostulacionRepository {
    suspend fun crearPostulacion(
        candidatoId: String,
        vacanteId: String,
        mensajePresentacion: String,
        linkedinUrl: String? = null,
        repoUrl: String? = null
    ): Boolean
    suspend fun obtenerPostulacionesCandidato(candidatoId: String): List<Postulacion>
    suspend fun obtenerPostulacionesVacante(vacanteId: String): List<Postulacion>
    suspend fun actualizarEstado(postulacionId: String, nuevoEstado: String): Boolean
    suspend fun obtenerProyectosReclutador(userId: String): List<Proyecto>
    suspend fun obtenerServiciosCandidato(userId: String): List<Proyecto>
}

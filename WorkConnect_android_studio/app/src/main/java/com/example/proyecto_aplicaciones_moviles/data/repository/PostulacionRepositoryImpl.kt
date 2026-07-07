package com.example.proyecto_aplicaciones_moviles.data.repository

import android.util.Log
import com.example.proyecto_aplicaciones_moviles.core.utils.SessionManager
import com.example.proyecto_aplicaciones_moviles.data.remote.PostulacionEstadoDto
import com.example.proyecto_aplicaciones_moviles.data.remote.PostulacionRequestDto
import com.example.proyecto_aplicaciones_moviles.data.remote.WorkConnectApi
import com.example.proyecto_aplicaciones_moviles.data.remote.toDomain
import com.example.proyecto_aplicaciones_moviles.domain.model.Postulacion
import com.example.proyecto_aplicaciones_moviles.domain.model.Proyecto
import com.example.proyecto_aplicaciones_moviles.domain.model.TipoOferta
import com.example.proyecto_aplicaciones_moviles.domain.repository.PostulacionRepository

class PostulacionRepositoryImpl(
    private val api: WorkConnectApi
) : PostulacionRepository {

    override suspend fun crearPostulacion(
        candidatoId: String,
        vacanteId: String,
        mensajePresentacion: String,
        linkedinUrl: String?,
        repoUrl: String?
    ): Boolean {
        return try {
            val response = api.crearPostulacion(
                PostulacionRequestDto(
                    candidatoId = candidatoId,
                    vacanteId = vacanteId,
                    mensajePresentacion = mensajePresentacion,
                    linkedinUrl = linkedinUrl?.takeIf { it.isNotBlank() },
                    repoUrl = repoUrl?.takeIf { it.isNotBlank() }
                )
            )
            if (response.code() == 409) {
                Log.w("POSTULACION_REPO", "Postulación duplicada — candidato ya postuló a esta vacante")
            }
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("POSTULACION_REPO", "Error al crear postulación: ${e.message}", e)
            false
        }
    }

    override suspend fun obtenerPostulacionesCandidato(candidatoId: String): List<Postulacion> {
        return try {
            val response = api.getPostulacionesCandidato(candidatoId)
            if (!response.isSuccessful) {
                Log.e("POSTULACION_REPO", "Error candidato: ${response.code()}")
                return emptyList()
            }

            val dtos = response.body()?.data?.items ?: return emptyList()

            val titulosPorId = dtos
                .mapNotNull { it.vacanteId }
                .distinct()
                .associate { vacanteId ->
                    val titulo = try {
                        val r = api.getProjectById(vacanteId)
                        if (r.isSuccessful) r.body()?.data?.titulo ?: "Sin título"
                        else "Sin título"
                    } catch (e: Exception) { "Sin título" }
                    vacanteId to titulo
                }

            dtos.map { dto ->
                val vacanteId = dto.vacanteId ?: ""
                Postulacion(
                    id = dto.id ?: "",
                    candidatoId = dto.candidatoId ?: "",
                    vacanteId = vacanteId,
                    mensajePresentacion = dto.mensajePresentacion ?: "",
                    estado = dto.estado ?: "postulado",
                    fechaPostulacion = dto.fechaPostulacion ?: "",
                    tituloVacante = titulosPorId[vacanteId] ?: "Sin título",
                    nombreCandidato = "",
                    linkedinUrl = dto.linkedinUrl,
                    repoUrl = dto.repoUrl
                )
            }
        } catch (e: Exception) {
            Log.e("POSTULACION_REPO", "Excepción candidato: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun obtenerPostulacionesVacante(vacanteId: String): List<Postulacion> {
        return try {
            val response = api.getPostulacionesVacante(vacanteId)
            if (!response.isSuccessful) {
                Log.e("POSTULACION_REPO", "Error vacante: ${response.code()}")
                return emptyList()
            }

            val dtos = response.body()?.data?.items ?: return emptyList()

            val usuarios = try {
                val r = api.getUsers()
                if (r.isSuccessful) {
                    r.body()?.data?.items?.associateBy { it.id } ?: emptyMap()
                } else emptyMap()
            } catch (e: Exception) { emptyMap() }

            dtos.map { dto ->
                val candidatoId = dto.candidatoId ?: ""
                val usuario = usuarios[candidatoId]
                val nombre = when {
                    usuario != null -> "${usuario.nombres ?: ""} ${usuario.apellidos ?: ""}".trim()
                        .ifBlank { "Candidato" }
                    else -> "Candidato"
                }

                Postulacion(
                    id = dto.id ?: "",
                    candidatoId = candidatoId,
                    vacanteId = dto.vacanteId ?: "",
                    mensajePresentacion = dto.mensajePresentacion ?: "",
                    estado = dto.estado ?: "postulado",
                    fechaPostulacion = dto.fechaPostulacion ?: "",
                    tituloVacante = "",
                    nombreCandidato = nombre,
                    linkedinUrl = dto.linkedinUrl,
                    repoUrl = dto.repoUrl
                )
            }
        } catch (e: Exception) {
            Log.e("POSTULACION_REPO", "Excepción vacante: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun actualizarEstado(postulacionId: String, nuevoEstado: String): Boolean {
        return try {
            val response = api.actualizarEstadoPostulacion(
                id = postulacionId,
                request = PostulacionEstadoDto(estado = nuevoEstado)
            )
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("POSTULACION_REPO", "Error al actualizar estado: ${e.message}", e)
            false
        }
    }

    override suspend fun obtenerServiciosCandidato(userId: String): List<Proyecto> {
        return try {
            val response = api.getProjects()
            if (!response.success) return emptyList()
            response.data.items
                .map { it.toDomain() }
                .filter { project ->
                    project.tipoOferta == TipoOferta.SERVICIO &&
                    project.creadoPorId != null && project.creadoPorId == userId
                }
        } catch (e: Exception) {
            Log.e("POSTULACION_REPO", "Error servicios candidato: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun obtenerProyectosReclutador(userId: String): List<Proyecto> {
        return try {
            val empresaNombre = SessionManager.currentEmpresaNombre ?: ""
            val response = api.getProjects()
            if (!response.success) return emptyList()

            response.data.items
                .map { it.toDomain() }
                .filter { project ->
                    project.tipoOferta == TipoOferta.TRABAJO && (
                        (project.creadoPorId != null && project.creadoPorId == userId) ||
                        (project.creadoPorId == null && empresaNombre.isNotBlank() && project.company == empresaNombre)
                    )
                }
        } catch (e: Exception) {
            Log.e("POSTULACION_REPO", "Error proyectos reclutador: ${e.message}", e)
            emptyList()
        }
    }
}
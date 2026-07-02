package com.example.proyecto_aplicaciones_moviles.data.repository

import android.util.Log
import com.example.proyecto_aplicaciones_moviles.data.remote.ProjectRequestDto
import com.example.proyecto_aplicaciones_moviles.data.remote.WorkConnectApi
import com.example.proyecto_aplicaciones_moviles.data.remote.toDomain
import com.example.proyecto_aplicaciones_moviles.domain.model.Project
import com.example.proyecto_aplicaciones_moviles.domain.repository.ProjectRepository

class ProjectRepositoryImpl(
    private val api: WorkConnectApi
) : ProjectRepository {

    // Carga proyectos y enriquece el campo "company" con el nombre real del publicador.
    // Si el proyecto tiene creadoPorId, buscamos el nombre del usuario en la lista de usuarios.
    // Esto resuelve el problema de proyectos que tienen "Mi Empresa" hardcodeado.
    override suspend fun obtenerProyectos(): List<Project> {
        return try {
            val response = api.getProjects()
            if (!response.success) return emptyList()

            val dtos = response.data.items

            // Traemos usuarios para resolver nombre del publicador
            val usuariosPorId: Map<String, com.example.proyecto_aplicaciones_moviles.data.remote.UserResponseDto> = try {
                val usersResponse = api.getUsers()
                if (usersResponse.isSuccessful) {
                    usersResponse.body()?.data?.items
                        ?.filter { it.id != null }
                        ?.associateBy { it.id!! }
                        ?: emptyMap()
                } else emptyMap()
            } catch (e: Exception) { emptyMap() }

            dtos.map { dto ->
                val project = dto.toDomain()
                val companyNombre = resolverNombrePublicador(
                    creadoPorId = dto.creadoPorId,
                    tipoOferta = dto.tipoOferta,
                    fallback = project.company,
                    usuariosPorId = usuariosPorId
                )
                project.copy(company = companyNombre)
            }
        } catch (e: Exception) {
            Log.e("AWS_ERROR", "Fallo la conexion a AWS: ${e.message}", e)
            emptyList()
        }
    }

    // Decide qué nombre mostrar según el tipo de oferta:
    // - "trabajo"  → nombre de empresa (reclutador busca candidatos)
    // - "servicio" → nombre personal (freelancer ofrece sus servicios)
    private fun resolverNombrePublicador(
        creadoPorId: String?,
        tipoOferta: String?,
        fallback: String,
        usuariosPorId: Map<String, com.example.proyecto_aplicaciones_moviles.data.remote.UserResponseDto>
    ): String {
        val usuario = creadoPorId?.let { usuariosPorId[it] } ?: return fallback
        val nombrePersonal = "${usuario.nombres ?: ""} ${usuario.apellidos ?: ""}".trim()
        val nombreEmpresa = usuario.empresaInfo?.nombre?.takeIf { it.isNotBlank() }

        return when (tipoOferta) {
            "servicio" -> nombrePersonal.takeIf { it.isNotBlank() } ?: fallback
            "trabajo"  -> nombreEmpresa ?: nombrePersonal.takeIf { it.isNotBlank() } ?: fallback
            else       -> nombreEmpresa ?: nombrePersonal.takeIf { it.isNotBlank() } ?: fallback
        }
    }

    override suspend fun obtenerProyectoPorId(id: String): Project? {
        return try {
            val response = api.getProjectById(id)
            if (!response.isSuccessful || response.body()?.data == null) {
                Log.e("AWS_ERROR", "Error al obtener proyecto: ${response.code()}")
                return null
            }

            val dto = response.body()!!.data!!
            val project = dto.toDomain()

            // Enriquecer con el nombre correcto según tipoOferta
            if (dto.creadoPorId != null) {
                try {
                    val usersResponse = api.getUsers()
                    if (usersResponse.isSuccessful) {
                        val usuariosPorId = usersResponse.body()?.data?.items
                            ?.filter { it.id != null }
                            ?.associateBy { it.id!! }
                            ?: emptyMap()
                        val companyNombre = resolverNombrePublicador(
                            creadoPorId = dto.creadoPorId,
                            tipoOferta = dto.tipoOferta,
                            fallback = project.company,
                            usuariosPorId = usuariosPorId
                        )
                        return project.copy(company = companyNombre)
                    }
                } catch (e: Exception) {
                    Log.e("AWS_ERROR", "Error al enriquecer publicador: ${e.message}")
                }
            }

            project
        } catch (e: Exception) {
            Log.e("AWS_ERROR", "Excepción al obtener proyecto: ${e.message}", e)
            null
        }
    }

    override suspend fun crearProyecto(request: ProjectRequestDto): Boolean {
        return try {
            api.createProject(request).isSuccessful
        } catch (e: Exception) {
            Log.e("AWS_ERROR", "Fallo al crear proyecto: ${e.message}", e)
            false
        }
    }

    // Trae la lista de nombres de categorías desde AWS para el dropdown
    override suspend fun obtenerCategorias(): List<String> {
        return try {
            val response = api.getCategorias()
            if (response.isSuccessful) {
                response.body()?.data?.items
                    ?.mapNotNull { it.nombre }
                    ?: emptyList()
            } else {
                Log.e("AWS_ERROR", "Error al traer categorías: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("AWS_ERROR", "Excepción al traer categorías: ${e.message}", e)
            emptyList()
        }
    }
}

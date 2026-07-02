package com.example.proyecto_aplicaciones_moviles.domain.repository

import com.example.proyecto_aplicaciones_moviles.data.remote.ProjectRequestDto
import com.example.proyecto_aplicaciones_moviles.domain.model.Project

interface ProjectRepository {
    suspend fun obtenerProyectos(): List<Project>
    suspend fun obtenerProyectoPorId(id: String): Project?
    suspend fun crearProyecto(request: ProjectRequestDto): Boolean
    suspend fun obtenerCategorias(): List<String>
}

package com.example.proyecto_aplicaciones_moviles.domain.repository
import com.example.proyecto_aplicaciones_moviles.domain.model.Project
import com.example.proyecto_aplicaciones_moviles.data.remote.ProjectRequestDto

interface ProjectRepository {
    suspend fun getProjects(): List<Project>

    // ¡NUEVO!
    suspend fun createProject(request: ProjectRequestDto): Boolean
}
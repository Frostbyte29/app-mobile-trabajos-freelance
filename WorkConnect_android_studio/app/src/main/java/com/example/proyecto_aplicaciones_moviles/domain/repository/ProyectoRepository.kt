package com.example.proyecto_aplicaciones_moviles.domain.repository

import com.example.proyecto_aplicaciones_moviles.data.remote.ProyectoRequestDto
import com.example.proyecto_aplicaciones_moviles.domain.model.Proyecto

interface ProyectoRepository {
    suspend fun obtenerProyectos(): List<Proyecto>
    suspend fun obtenerProyectoPorId(id: String): Proyecto?
    suspend fun crearProyecto(request: ProyectoRequestDto): Boolean
    suspend fun obtenerCategorias(): List<String>
}
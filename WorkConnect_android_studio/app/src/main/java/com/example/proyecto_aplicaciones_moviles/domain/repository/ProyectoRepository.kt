package com.example.proyecto_aplicaciones_moviles.domain.repository
import com.example.proyecto_aplicaciones_moviles.domain.model.Proyecto
import com.example.proyecto_aplicaciones_moviles.data.remote.ProyectoRequestDto

interface ProyectoRepository {
    suspend fun getProyectos(): List<Proyecto>

    // ¡NUEVO!
    suspend fun crearPoyectos(request: ProyectoRequestDto): Boolean
}
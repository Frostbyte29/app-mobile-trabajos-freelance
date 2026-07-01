package com.example.proyecto_aplicaciones_moviles.data.repository

import com.example.proyecto_aplicaciones_moviles.data.remote.WorkConnectApi
import com.example.proyecto_aplicaciones_moviles.data.remote.toDomain
import com.example.proyecto_aplicaciones_moviles.domain.model.Proyecto
import com.example.proyecto_aplicaciones_moviles.domain.repository.ProyectoRepository
import android.util.Log
import com.example.proyecto_aplicaciones_moviles.data.remote.ProyectoRequestDto

class ProyectoRepositoryImpl(
    private val api: WorkConnectApi
) : ProyectoRepository {

    override suspend fun getProyectos(): List<Proyecto> {
        return try {
            val response = api.getProjects()

            // Navegamos por el JSON: respuesta -> data -> items
            if (response.success) {
                response.data.items.map { it.toDomain() }
            } else {
                emptyList()
            }

        } catch (e: Exception) {
            Log.e("AWS_ERROR", "Fallo la conexion a AWS: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun crearPoyectos(request: ProyectoRequestDto): Boolean {
        return try {
            val response = api.createProject(request)
            response.isSuccessful // Devolverá true si AWS lo guardó correctamente
        } catch (e: Exception) {
            Log.e("AWS_ERROR", "Fallo al crear proyecto: ${e.message}", e)
            false
        }
    }
}
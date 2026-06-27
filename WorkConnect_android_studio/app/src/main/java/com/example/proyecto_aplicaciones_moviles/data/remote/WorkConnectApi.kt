package com.example.proyecto_aplicaciones_moviles.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface WorkConnectApi {
    // El que ya tenías para leer
    @GET("projects")
    suspend fun getProjects(): ProjectResponseWrapper

    // ¡NUEVO! El que usamos para publicar
    @POST("projects")
    suspend fun createProject(@Body request: ProjectRequestDto): Response<Any>
    // Usamos Response<Any> para que solo nos importe si fue exitoso (Código 200) y no se confunda con el JSON de respuesta.
}
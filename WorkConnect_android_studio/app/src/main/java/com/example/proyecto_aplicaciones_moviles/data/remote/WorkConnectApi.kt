package com.example.proyecto_aplicaciones_moviles.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface WorkConnectApi {
    // El que ya tenías para leer
    @GET("projects")
    suspend fun getProjects(): ProyectoResponseWrapper

    // ¡NUEVO! El que usamos para publicar
    @POST("projects")
    suspend fun createProject(@Body request: ProyectoRequestDto): Response<Any>
    // Usamos Response<Any> para que solo nos importe si fue exitoso (Código 200) y no se confunda con el JSON de respuesta.
    // Nota: Revisa en Postman si la ruta exacta es "users", "usuarios" o "usuarios/candidato" y cámbiala aquí si es necesario
    @POST("usuarios")
    suspend fun registerCandidate(@Body request: UsuarioRequestDto): Response<Any>

    // ¡NUEVO! Traer todos los usuarios para validar el Login
    @GET("usuarios")
    suspend fun getUsers(): Response<UsuarioListResponseWrapper>
}
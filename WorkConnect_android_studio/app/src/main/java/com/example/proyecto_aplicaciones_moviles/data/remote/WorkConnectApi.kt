package com.example.proyecto_aplicaciones_moviles.data.remote

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface WorkConnectApi {

    // --- PROYECTOS ---

    @GET("projects")
    suspend fun getProjects(): ProjectResponseWrapper

    // Obtener un proyecto por su ID — para resolver título al mostrar postulaciones
    @GET("projects/{id}")
    suspend fun getProjectById(@retrofit2.http.Path("id") id: String): retrofit2.Response<ProjectSingleResponseWrapper>

    // ResponseBody en lugar de Any — acepta cualquier respuesta JSON sin fallar al parsear
    @POST("projects")
    suspend fun createProject(@Body request: ProjectRequestDto): Response<ResponseBody>

    // --- USUARIOS ---

    // ResponseBody evita el error de parseo cuando el backend devuelve 201 con body
    @POST("usuarios")
    suspend fun registerCandidate(@Body request: UserRequestDto): Response<ResponseBody>

    // Traemos hasta 100 usuarios para el login — evita problema de paginación
    @GET("usuarios")
    suspend fun getUsers(
        @Query("limit") limit: Int = 100
    ): Response<UserListResponseWrapper>

    @GET("usuarios/{id}")
    suspend fun getUserById(@Path("id") id: String): Response<UserSingleResponseWrapper>

    @PUT("usuarios/{id}")
    suspend fun updateUser(
        @Path("id") id: String,
        @Body request: UserUpdateRolesDto
    ): Response<ResponseBody>

    @PUT("usuarios/{id}")
    suspend fun updateUserData(
        @Path("id") id: String,
        @Body request: UserUpdateDataDto
    ): Response<ResponseBody>

    // --- CATEGORÍAS ---

    @GET("categorias")
    suspend fun getCategorias(): Response<CategoriaListResponseWrapper>

    // --- POSTULACIONES ---

    @POST("postulaciones")
    suspend fun crearPostulacion(@Body request: PostulacionRequestDto): Response<ResponseBody>

    @GET("postulaciones")
    suspend fun getPostulacionesCandidato(
        @Query("candidatoId") candidatoId: String,
        @Query("limit") limit: Int = 20
    ): Response<PostulacionListResponseWrapper>

    @GET("postulaciones")
    suspend fun getPostulacionesVacante(
        @Query("vacanteId") vacanteId: String,
        @Query("limit") limit: Int = 20
    ): Response<PostulacionListResponseWrapper>

    @PUT("postulaciones/{id}")
    suspend fun actualizarEstadoPostulacion(
        @Path("id") id: String,
        @Body request: PostulacionEstadoDto
    ): Response<ResponseBody>

    // --- VALORACIONES ---

    // Crear valoración/comentario sobre un usuario en el contexto de una oferta
    @POST("valoraciones")
    suspend fun crearValoracion(@Body request: ValoracionRequestDto): Response<ResponseBody>

    // Listar valoraciones recibidas por un usuario
    @GET("valoraciones")
    suspend fun getValoraciones(
        @Query("usuarioReceptorId") usuarioReceptorId: String,
        @Query("limit") limit: Int = 20
    ): Response<ValoracionListResponseWrapper>

    // --- NOTIFICACIONES ---

    @GET("notificaciones")
    suspend fun getNotificaciones(
        @Query("usuarioId") usuarioId: String,
        @Query("limit") limit: Int = 30
    ): Response<NotificacionListResponseWrapper>

    @PUT("notificaciones/{id}")
    suspend fun marcarNotificacionLeida(
        @Path("id") id: String
    ): Response<ResponseBody>
}

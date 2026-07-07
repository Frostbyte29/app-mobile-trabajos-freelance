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

    // PROYECTOS

    @GET("projects")
    suspend fun getProjects(): ProyectoResponseWrapper

    // Obtener un proyecto por su ID
    @GET("projects/{id}")
    suspend fun getProjectById(@retrofit2.http.Path("id") id: String): retrofit2.Response<ProyectotSingleResponseWrapper>

    @POST("projects")
    suspend fun createProject(@Body request: ProyectoRequestDto): Response<ResponseBody>

    // USUARIOS

    @POST("usuarios")
    suspend fun registerCandidate(@Body request: UsuarioRequestDto): Response<ResponseBody>

    @GET("usuarios")
    suspend fun getUsers(
        @Query("limit") limit: Int = 100
    ): Response<UsuarioListResponseWrapper>

    @GET("usuarios/{id}")
    suspend fun getUserById(@Path("id") id: String): Response<UsuarioSingleResponseWrapper>

    @PUT("usuarios/{id}")
    suspend fun updateUser(
        @Path("id") id: String,
        @Body request: UsuarioUpdateRolesDto
    ): Response<ResponseBody>

    @PUT("usuarios/{id}")
    suspend fun updateUserData(
        @Path("id") id: String,
        @Body request: UsuarioUpdateDataDto
    ): Response<ResponseBody>

    // CATEGORÍAS

    @GET("categorias")
    suspend fun getCategorias(): Response<CategoriaListResponseWrapper>

    // POSTULACIONES

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

    // VALORACIONES

    // Crear valoración
    @POST("valoraciones")
    suspend fun crearValoracion(@Body request: ValoracionRequestDto): Response<ResponseBody>

    // Editar una valoración
    @PUT("valoraciones/{id}")
    suspend fun actualizarValoracion(
        @Path("id") id: String,
        @Body request: ValoracionUpdateDto
    ): Response<ResponseBody>

    // Listar valoraciones
    @GET("valoraciones")
    suspend fun getValoraciones(
        @Query("usuarioReceptorId") usuarioReceptorId: String,
        @Query("limit") limit: Int = 20
    ): Response<ValoracionListResponseWrapper>

    // NOTIFICACIONES

    @GET("notificaciones")
    suspend fun getNotificaciones(
        @Query("usuarioId") usuarioId: String,
        @Query("limit") limit: Int = 30
    ): Response<NotificacionListResponseWrapper>

    @PUT("notificaciones/{id}")
    suspend fun marcarNotificacionLeida(
        @Path("id") id: String
    ): Response<ResponseBody>
    // CONTRATOS

    @POST("contratos")
    suspend fun crearContrato(@Body request: ContratoRequestDto): Response<ResponseBody>

    @PUT("contratos/{id}")
    suspend fun finalizarContrato(
        @Path("id") id: String,
        @Body request: ContratoEstadoDto
    ): Response<ResponseBody>

    @GET("contratos")
    suspend fun getContratosPorFreelancer(
        @Query("freelancerId") freelancerId: String
    ): Response<ContratoListResponseWrapper>

    @GET("contratos")
    suspend fun getContratosPorContratante(
        @Query("contratanteId") contratanteId: String
    ): Response<ContratoListResponseWrapper>

    // CONVERSACIONES

    @POST("conversaciones")
    suspend fun crearConversacion(
        @Body request: ConversacionRequestDto
    ): Response<ConversacionSingleResponseWrapper>

    @GET("conversaciones")
    suspend fun getConversaciones(
        @Query("usuarioId") usuarioId: String
    ): Response<ConversacionListResponseWrapper>

    @GET("conversaciones/{id}/mensajes")
    suspend fun getMensajes(
        @Path("id") conversationId: String,
        @Query("limit") limit: Int = 100
    ): Response<MensajeListResponseWrapper>

    @POST("conversaciones/{id}/mensajes")
    suspend fun enviarMensaje(
        @Path("id") conversationId: String,
        @Body request: MensajeRequestDto
    ): Response<ResponseBody>
}
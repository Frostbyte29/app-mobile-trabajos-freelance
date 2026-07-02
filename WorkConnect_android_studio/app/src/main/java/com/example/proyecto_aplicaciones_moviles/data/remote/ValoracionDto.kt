package com.example.proyecto_aplicaciones_moviles.data.remote

import com.google.gson.annotations.SerializedName

// Molde para crear una valoración/comentario
data class ValoracionRequestDto(
    @SerializedName("usuarioEmisorId") val usuarioEmisorId: String,
    @SerializedName("usuarioReceptorId") val usuarioReceptorId: String,
    @SerializedName("proyectoId") val proyectoId: String? = null,
    @SerializedName("puntuacion") val puntuacion: Int,          // 1 a 5
    @SerializedName("comentario") val comentario: String? = null
)

// Respuesta de una valoración individual
data class ValoracionDto(
    @SerializedName("id") val id: String?,
    @SerializedName("usuarioEmisorId") val usuarioEmisorId: String?,
    @SerializedName("usuarioReceptorId") val usuarioReceptorId: String?,
    @SerializedName("proyectoId") val proyectoId: String?,
    @SerializedName("puntuacion") val puntuacion: Int?,
    @SerializedName("comentario") val comentario: String?,
    @SerializedName("fechaCreacion") val fechaCreacion: String?,
    @SerializedName("editada") val editada: Boolean? = false
)

// Wrapper de lista de valoraciones
data class ValoracionListResponseWrapper(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: ValoracionDataWrapper?
)

data class ValoracionDataWrapper(
    @SerializedName("items") val items: List<ValoracionDto>?,
    @SerializedName("nextKey") val nextKey: String?
)

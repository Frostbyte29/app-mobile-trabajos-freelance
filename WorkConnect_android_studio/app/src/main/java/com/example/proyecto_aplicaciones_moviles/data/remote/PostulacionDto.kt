package com.example.proyecto_aplicaciones_moviles.data.remote

import com.google.gson.annotations.SerializedName

// Molde para crear una postulación (POST /postulaciones)
data class PostulacionRequestDto(
    @SerializedName("candidatoId") val candidatoId: String,
    @SerializedName("vacanteId") val vacanteId: String,
    @SerializedName("mensajePresentacion") val mensajePresentacion: String? = null,
    @SerializedName("cvUrl") val cvUrl: String? = null
)

// Molde de respuesta de una postulación individual
data class PostulacionDto(
    @SerializedName("id") val id: String?,
    @SerializedName("candidatoId") val candidatoId: String?,
    @SerializedName("vacanteId") val vacanteId: String?,
    @SerializedName("mensajePresentacion") val mensajePresentacion: String?,
    @SerializedName("cvUrl") val cvUrl: String?,
    @SerializedName("estado") val estado: String?,          // "postulado", "en_revision", "aceptado", "rechazado"
    @SerializedName("fechaPostulacion") val fechaPostulacion: String?,
    @SerializedName("fechaActualizacion") val fechaActualizacion: String?
)

// Caja principal de la lista de postulaciones
data class PostulacionListResponseWrapper(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: PostulacionDataWrapper?
)

data class PostulacionDataWrapper(
    @SerializedName("items") val items: List<PostulacionDto>?,
    @SerializedName("nextKey") val nextKey: String?
)

// Molde para cambiar el estado de una postulación (PUT /postulaciones/{id})
data class PostulacionEstadoDto(
    @SerializedName("estado") val estado: String
)

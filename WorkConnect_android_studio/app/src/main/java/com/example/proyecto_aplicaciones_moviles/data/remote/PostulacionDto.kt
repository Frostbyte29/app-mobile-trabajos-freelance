package com.example.proyecto_aplicaciones_moviles.data.remote

import com.google.gson.annotations.SerializedName

data class PostulacionRequestDto(
    @SerializedName("candidatoId") val candidatoId: String,
    @SerializedName("vacanteId") val vacanteId: String,
    @SerializedName("mensajePresentacion") val mensajePresentacion: String? = null,
    @SerializedName("cvUrl") val cvUrl: String? = null,
    @SerializedName("linkedinUrl") val linkedinUrl: String? = null,
    @SerializedName("repoUrl") val repoUrl: String? = null
)

data class PostulacionDto(
    @SerializedName("id") val id: String?,
    @SerializedName("candidatoId") val candidatoId: String?,
    @SerializedName("vacanteId") val vacanteId: String?,
    @SerializedName("mensajePresentacion") val mensajePresentacion: String?,
    @SerializedName("cvUrl") val cvUrl: String?,
    @SerializedName("linkedinUrl") val linkedinUrl: String?,
    @SerializedName("repoUrl") val repoUrl: String?,
    @SerializedName("estado") val estado: String?,
    @SerializedName("fechaPostulacion") val fechaPostulacion: String?,
    @SerializedName("fechaActualizacion") val fechaActualizacion: String?
)

data class PostulacionListResponseWrapper(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: PostulacionDataWrapper?
)

data class PostulacionDataWrapper(
    @SerializedName("items") val items: List<PostulacionDto>?,
    @SerializedName("nextKey") val nextKey: String?
)

data class PostulacionEstadoDto(
    @SerializedName("estado") val estado: String
)

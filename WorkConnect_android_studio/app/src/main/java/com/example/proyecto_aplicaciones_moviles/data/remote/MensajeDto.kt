package com.example.proyecto_aplicaciones_moviles.data.remote

import com.google.gson.annotations.SerializedName

data class MensajeDto(
    @SerializedName("id") val id: String?,
    @SerializedName("emisorId") val emisorId: String?,
    @SerializedName("contenido") val contenido: String?,
    @SerializedName("archivoUrl") val archivoUrl: String?,
    @SerializedName("leido") val leido: Boolean?,
    @SerializedName("fechaEnvio") val fechaEnvio: String?
)

data class MensajeListResponseWrapper(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: MensajeDataWrapper?
)

data class MensajeDataWrapper(
    @SerializedName("items") val items: List<MensajeDto>?,
    @SerializedName("nextKey") val nextKey: String?
)

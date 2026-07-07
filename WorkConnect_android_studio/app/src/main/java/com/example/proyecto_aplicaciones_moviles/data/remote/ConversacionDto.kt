package com.example.proyecto_aplicaciones_moviles.data.remote

import com.google.gson.annotations.SerializedName

data class ConversacionDto(
    @SerializedName("id") val id: String?,
    @SerializedName("vacanteId") val vacanteId: String?,
    @SerializedName("participanteAId") val participanteAId: String?,
    @SerializedName("participanteBId") val participanteBId: String?,
    @SerializedName("activa") val activa: Boolean?,
    @SerializedName("fechaCreacion") val fechaCreacion: String?
)

data class ConversacionListResponseWrapper(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: ConversacionDataWrapper?
)

data class ConversacionDataWrapper(
    @SerializedName("items") val items: List<ConversacionDto>?,
    @SerializedName("nextKey") val nextKey: String?
)

data class ConversacionRequestDto(
    @SerializedName("participanteAId") val participanteAId: String,
    @SerializedName("participanteBId") val participanteBId: String,
    @SerializedName("vacanteId") val vacanteId: String? = null
)

data class ConversacionSingleResponseWrapper(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: ConversacionDto?
)

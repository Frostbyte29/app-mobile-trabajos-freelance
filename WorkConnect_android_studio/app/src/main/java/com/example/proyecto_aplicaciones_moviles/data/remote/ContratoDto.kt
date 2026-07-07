package com.example.proyecto_aplicaciones_moviles.data.remote

import com.google.gson.annotations.SerializedName

data class ContratoDto(
    @SerializedName("id") val id: String?,
    @SerializedName("contratanteId") val contratanteId: String?,
    @SerializedName("freelancerId") val freelancerId: String?,
    @SerializedName("ofertaId") val ofertaId: String?,
    @SerializedName("tituloOferta") val tituloOferta: String?,
    @SerializedName("tipoOrigen") val tipoOrigen: String?,
    @SerializedName("estado") val estado: String?,
    @SerializedName("fechaInicio") val fechaInicio: String?,
    @SerializedName("fechaFin") val fechaFin: String?,
    @SerializedName("postulacionId") val postulacionId: String?
)

data class ContratoListResponseWrapper(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: ContratoDataWrapper?
)

data class ContratoDataWrapper(
    @SerializedName("items") val items: List<ContratoDto>?,
    @SerializedName("nextKey") val nextKey: String?
)

data class ContratoRequestDto(
    @SerializedName("contratanteId") val contratanteId: String,
    @SerializedName("freelancerId") val freelancerId: String,
    @SerializedName("ofertaId") val ofertaId: String,
    @SerializedName("tituloOferta") val tituloOferta: String,
    @SerializedName("tipoOrigen") val tipoOrigen: String,
    @SerializedName("postulacionId") val postulacionId: String? = null
)

data class ContratoEstadoDto(
    @SerializedName("estado") val estado: String
)

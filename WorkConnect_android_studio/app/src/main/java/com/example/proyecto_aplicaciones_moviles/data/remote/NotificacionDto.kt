package com.example.proyecto_aplicaciones_moviles.data.remote

import com.google.gson.annotations.SerializedName

data class NotificacionDto(
    @SerializedName("id")            val id: String?,
    @SerializedName("usuarioId")     val usuarioId: String?,
    @SerializedName("titulo")        val titulo: String?,
    @SerializedName("mensaje")       val mensaje: String?,
    @SerializedName("tipo")          val tipo: String?,
    @SerializedName("referenciaId")  val referenciaId: String?,
    @SerializedName("leida")         val leida: Boolean?,
    @SerializedName("fechaCreacion") val fechaCreacion: String?
)

data class NotificacionListResponseWrapper(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data")    val data: NotificacionDataWrapper?
)

data class NotificacionDataWrapper(
    @SerializedName("items")   val items: List<NotificacionDto>?,
    @SerializedName("nextKey") val nextKey: String?
)

package com.example.proyecto_aplicaciones_moviles.data.remote

import com.example.proyecto_aplicaciones_moviles.domain.model.Proyecto
import com.example.proyecto_aplicaciones_moviles.domain.model.TipoOferta
import com.google.gson.annotations.SerializedName

data class ProyectoResponseWrapper(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: ProyectotData,
    @SerializedName("timestamp") val timestamp: String
)

data class ProyectotData(
    @SerializedName("items") val items: List<ProyectotDto>,
    @SerializedName("nextKey") val nextKey: String?
)

data class ProyectotDto(
    @SerializedName("id") val id: String?,
    @SerializedName("titulo") val titulo: String?,
    @SerializedName("descripcion") val descripcion: String?,
    @SerializedName("presupuesto") val presupuesto: Double?,
    @SerializedName("categoria") val categoria: String?,
    @SerializedName("empresa") val empresa: String?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("creadoPorId") val creadoPorId: String? = null,
    @SerializedName("tipoOferta") val tipoOferta: String? = null
)

data class ProyectoRequestDto(
    @SerializedName("titulo") val titulo: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("presupuesto") val presupuesto: Double,
    @SerializedName("categoria") val categoria: String,
    @SerializedName("empresa") val empresa: String,
    @SerializedName("tipoOferta") val tipoOferta: String,
    @SerializedName("creadoPorId") val creadoPorId: String? = null
)

fun ProyectotDto.toDomain(): Proyecto {
    val tipo = when (tipoOferta) {
        "servicio" -> TipoOferta.SERVICIO
        else -> TipoOferta.TRABAJO
    }
    return Proyecto(
        id = this.id ?: "",
        title = this.titulo ?: "Sin título",
        description = this.descripcion ?: "Sin descripción",
        budget = this.presupuesto ?: 0.0,
        category = this.categoria ?: "General",
        company = this.empresa ?: "",
        tipoOferta = tipo,
        creadoPorId = this.creadoPorId,
        createdAt = this.createdAt ?: ""
    )
}

data class ProyectotSingleResponseWrapper(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: ProyectotDto?
)
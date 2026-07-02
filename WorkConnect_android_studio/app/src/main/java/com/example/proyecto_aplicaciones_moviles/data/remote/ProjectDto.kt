package com.example.proyecto_aplicaciones_moviles.data.remote

import com.example.proyecto_aplicaciones_moviles.domain.model.Project
import com.example.proyecto_aplicaciones_moviles.domain.model.TipoOferta
import com.google.gson.annotations.SerializedName

data class ProjectResponseWrapper(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: ProjectData,
    @SerializedName("timestamp") val timestamp: String
)

data class ProjectData(
    @SerializedName("items") val items: List<ProjectDto>,
    @SerializedName("nextKey") val nextKey: String?
)

data class ProjectDto(
    @SerializedName("id") val id: String?,
    @SerializedName("titulo") val titulo: String?,
    @SerializedName("descripcion") val descripcion: String?,
    @SerializedName("presupuesto") val presupuesto: Double?,
    @SerializedName("categoria") val categoria: String?,
    @SerializedName("empresa") val empresa: String?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("creadoPorId") val creadoPorId: String? = null,
    // "trabajo" o "servicio" — distingue el tipo de oferta
    @SerializedName("tipoOferta") val tipoOferta: String? = null
)

data class ProjectRequestDto(
    @SerializedName("titulo") val titulo: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("presupuesto") val presupuesto: Double,
    @SerializedName("categoria") val categoria: String,
    @SerializedName("empresa") val empresa: String,
    @SerializedName("tipoOferta") val tipoOferta: String,   // "trabajo" o "servicio"
    @SerializedName("creadoPorId") val creadoPorId: String? = null
)

fun ProjectDto.toDomain(): Project {
    val tipo = when (tipoOferta) {
        "servicio" -> TipoOferta.SERVICIO
        else -> TipoOferta.TRABAJO
    }
    return Project(
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

data class ProjectSingleResponseWrapper(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: ProjectDto?
)

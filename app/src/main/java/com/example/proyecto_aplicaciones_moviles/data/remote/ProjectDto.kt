package com.example.proyecto_aplicaciones_moviles.data.remote

import com.example.proyecto_aplicaciones_moviles.domain.model.Project
import com.google.gson.annotations.SerializedName

// 1. El envoltorio principal de AWS
data class ProjectResponseWrapper(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: ProjectData,
    @SerializedName("timestamp") val timestamp: String
)

// 2. La sección "data" que guarda la lista
data class ProjectData(
    @SerializedName("items") val items: List<ProjectDto>,
    @SerializedName("nextKey") val nextKey: String?
)

// 3. Nuestro proyecto tal cual viene de AWS
data class ProjectDto(
    @SerializedName("id") val id: String?,
    @SerializedName("titulo") val titulo: String?,
    @SerializedName("descripcion") val descripcion: String?,
    @SerializedName("presupuesto") val presupuesto: Double?,
    @SerializedName("categoria") val categoria: String?,
    @SerializedName("empresa") val empresa: String?,
    @SerializedName("createdAt") val createdAt: String?
)

// Agrega esto al final de tu archivo ProjectDto.kt
data class ProjectRequestDto(
    @SerializedName("titulo") val titulo: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("presupuesto") val presupuesto: Double,
    @SerializedName("categoria") val categoria: String,
    @SerializedName("empresa") val empresa: String
)

// Función para mapearlo a la interfaz
fun ProjectDto.toDomain(): Project {
    return Project(
        id = this.id ?: "",
        title = this.titulo ?: "Sin título",
        description = this.descripcion ?: "Sin descripción",
        budget = this.presupuesto ?: 0.0,
        category = this.categoria ?: "General",
        company = this.empresa ?: "Empresa confidencial"
    )


}
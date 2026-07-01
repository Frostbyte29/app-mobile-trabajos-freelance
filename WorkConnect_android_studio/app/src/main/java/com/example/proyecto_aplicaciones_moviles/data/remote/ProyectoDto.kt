package com.example.proyecto_aplicaciones_moviles.data.remote

import com.example.proyecto_aplicaciones_moviles.domain.model.Proyecto
import com.google.gson.annotations.SerializedName

// 1. El envoltorio principal de AWS
data class ProyectoResponseWrapper(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: ProyectotData,
    @SerializedName("timestamp") val timestamp: String
)

// 2. La sección "data" que guarda la lista
data class ProyectotData(
    @SerializedName("items") val items: List<ProyectoDto>,
    @SerializedName("nextKey") val nextKey: String?
)

// 3. Nuestro proyecto tal cual viene de AWS
data class ProyectoDto(
    @SerializedName("id") val id: String?,
    @SerializedName("titulo") val titulo: String?,
    @SerializedName("descripcion") val descripcion: String?,
    @SerializedName("presupuesto") val presupuesto: Double?,
    @SerializedName("categoria") val categoria: String?,
    @SerializedName("empresa") val empresa: String?,
    @SerializedName("createdAt") val createdAt: String?
)

// Agrega esto al final de tu archivo ProyectoDto.kt
data class ProyectoRequestDto(
    @SerializedName("titulo") val titulo: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("presupuesto") val presupuesto: Double,
    @SerializedName("categoria") val categoria: String,
    @SerializedName("empresa") val empresa: String
)

// Función para mapearlo a la interfaz
fun ProyectoDto.toDomain(): Proyecto {
    return Proyecto(
        id = this.id ?: "",
        title = this.titulo ?: "Sin título",
        description = this.descripcion ?: "Sin descripción",
        budget = this.presupuesto ?: 0.0,
        category = this.categoria ?: "General",
        company = this.empresa ?: "Empresa confidencial"
    )


}
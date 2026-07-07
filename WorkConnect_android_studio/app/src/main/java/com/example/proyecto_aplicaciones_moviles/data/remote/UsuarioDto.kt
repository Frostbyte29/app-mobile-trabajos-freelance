package com.example.proyecto_aplicaciones_moviles.data.remote

import com.google.gson.annotations.SerializedName

data class EmpresaInfoDto(
    @SerializedName("nombre") val nombre: String? = null,
    @SerializedName("rubro") val rubro: String? = null,
    @SerializedName("correoContacto") val correoContacto: String? = null,
    @SerializedName("telefono") val telefono: String? = null,
    @SerializedName("sitioWeb") val sitioWeb: String? = null,
    @SerializedName("direccion") val direccion: String? = null
)

data class UsuarioRequestDto(
    @SerializedName("nombres") val nombres: String,
    @SerializedName("apellidos") val apellidos: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("telefono") val telefono: String,
    @SerializedName("roles") val roles: List<String>,
    @SerializedName("empresaInfo") val empresaInfo: EmpresaInfoDto? = null
)

data class UsuarioResponseDto(
    @SerializedName("id") val id: String?,
    @SerializedName("nombres") val nombres: String?,
    @SerializedName("apellidos") val apellidos: String?,
    @SerializedName("correo") val correo: String?,
    @SerializedName("telefono") val telefono: String?,
    @SerializedName("fotoPerfilUrl") val fotoPerfilUrl: String?,
    @SerializedName("acercaDe") val acercaDe: String? = null,
    @SerializedName("roles") val roles: List<String>?,
    @SerializedName("empresaInfo") val empresaInfo: EmpresaInfoDto? = null
)

data class UsuarioListResponseWrapper(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: UsuarioDataWrapper?
)

data class UsuarioDataWrapper(
    @SerializedName("items") val items: List<UsuarioResponseDto>?
)

data class UsuarioSingleResponseWrapper(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: UsuarioResponseDto?
)

data class UsuarioUpdateRolesDto(
    @SerializedName("roles") val roles: List<String>
)

data class UsuarioUpdateDataDto(
    @SerializedName("nombres") val nombres: String,
    @SerializedName("apellidos") val apellidos: String,
    @SerializedName("telefono") val telefono: String,
    @SerializedName("acercaDe") val acercaDe: String? = null,
    @SerializedName("roles") val roles: List<String>, // requerido por el schema del backend
    @SerializedName("empresaInfo") val empresaInfo: EmpresaInfoDto? = null
)

data class CategoriaDto(
    @SerializedName("id") val id: String?,
    @SerializedName("nombre") val nombre: String?,
    @SerializedName("descripcion") val descripcion: String?
)

data class CategoriaListResponseWrapper(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: CategoriaDataWrapper?
)

data class CategoriaDataWrapper(
    @SerializedName("items") val items: List<CategoriaDto>?
)
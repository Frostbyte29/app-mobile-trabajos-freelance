package com.example.proyecto_aplicaciones_moviles.data.remote

import com.google.gson.annotations.SerializedName

// Datos de empresa embebidos en el usuario reclutador
// Todos opcionales porque el usuario puede tenerlos incompletos al inicio
data class EmpresaInfoDto(
    @SerializedName("nombre") val nombre: String? = null,
    @SerializedName("rubro") val rubro: String? = null,
    @SerializedName("correoContacto") val correoContacto: String? = null,
    @SerializedName("telefono") val telefono: String? = null,
    @SerializedName("sitioWeb") val sitioWeb: String? = null,
    @SerializedName("direccion") val direccion: String? = null
)

// El molde exacto para enviar el registro a AWS
data class UserRequestDto(
    @SerializedName("nombres") val nombres: String,
    @SerializedName("apellidos") val apellidos: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("telefono") val telefono: String,
    @SerializedName("roles") val roles: List<String>,
    @SerializedName("empresaInfo") val empresaInfo: EmpresaInfoDto? = null
)

// El molde de respuesta de AWS con datos del usuario
data class UserResponseDto(
    @SerializedName("id") val id: String?,
    @SerializedName("nombres") val nombres: String?,
    @SerializedName("apellidos") val apellidos: String?,
    @SerializedName("correo") val correo: String?,
    @SerializedName("telefono") val telefono: String?,
    @SerializedName("fotoPerfilUrl") val fotoPerfilUrl: String?,
    @SerializedName("roles") val roles: List<String>?,
    @SerializedName("empresaInfo") val empresaInfo: EmpresaInfoDto? = null
)

// Caja principal que recibe AWS al listar usuarios
data class UserListResponseWrapper(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: UserDataWrapper?
)

// Sub-caja con la lista real
data class UserDataWrapper(
    @SerializedName("items") val items: List<UserResponseDto>?
)

// Caja para UN solo usuario por ID
data class UserSingleResponseWrapper(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: UserResponseDto?
)

// Molde para actualizar solo los roles del usuario
data class UserUpdateRolesDto(
    @SerializedName("roles") val roles: List<String>
)

// Molde para actualizar datos personales + empresa del usuario (editar perfil)
data class UserUpdateDataDto(
    @SerializedName("nombres") val nombres: String,
    @SerializedName("apellidos") val apellidos: String,
    @SerializedName("telefono") val telefono: String,
    @SerializedName("roles") val roles: List<String>, // requerido por el schema del backend
    @SerializedName("empresaInfo") val empresaInfo: EmpresaInfoDto? = null
)

// DTOs para categorías
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

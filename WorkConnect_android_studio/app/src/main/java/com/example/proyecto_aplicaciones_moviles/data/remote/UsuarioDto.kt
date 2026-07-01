package com.example.proyecto_aplicaciones_moviles.data.remote

import com.google.gson.annotations.SerializedName

// El molde exacto para enviar el registro a AWS
data class UsuarioRequestDto(
    @SerializedName("nombres") val nombres: String,
    @SerializedName("apellidos") val apellidos: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("telefono") val telefono: String,
    @SerializedName("roles") val roles: List<String>
)

// El molde por si AWS te responde con los datos del usuario creado o un ID
data class UsuarioResponseDto(
    @SerializedName("id") val id: String?,
    @SerializedName("correo") val correo: String?,
    @SerializedName("roles") val roles: List<String>?
)

// 1. La caja principal que recibe AWS
data class UsuarioListResponseWrapper(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: UsuarioDataWrapper?
)

// 2. La sub-caja que contiene la lista real
data class UsuarioDataWrapper(
    @SerializedName("items") val items: List<UsuarioResponseDto>?
)
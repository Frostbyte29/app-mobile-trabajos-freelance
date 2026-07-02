package com.example.proyecto_aplicaciones_moviles.domain.repository

import com.example.proyecto_aplicaciones_moviles.data.remote.EmpresaInfoDto
import com.example.proyecto_aplicaciones_moviles.data.remote.UserResponseDto

interface ProfileRepository {
    suspend fun obtenerUsuarioPorCorreo(email: String): UserResponseDto?
    suspend fun agregarRolAUsuario(userId: String, currentRoles: List<String>, newRole: String): Boolean
    suspend fun actualizarDatosUsuario(
        userId: String,
        nombres: String,
        apellidos: String,
        telefono: String,
        roles: List<String>,
        empresaInfo: EmpresaInfoDto? = null
    ): Boolean
}

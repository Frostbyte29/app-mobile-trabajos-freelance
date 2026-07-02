package com.example.proyecto_aplicaciones_moviles.data.repository

import android.util.Log
import com.example.proyecto_aplicaciones_moviles.data.remote.EmpresaInfoDto
import com.example.proyecto_aplicaciones_moviles.data.remote.UserResponseDto
import com.example.proyecto_aplicaciones_moviles.data.remote.UserUpdateDataDto
import com.example.proyecto_aplicaciones_moviles.data.remote.UserUpdateRolesDto
import com.example.proyecto_aplicaciones_moviles.data.remote.WorkConnectApi
import com.example.proyecto_aplicaciones_moviles.domain.repository.ProfileRepository

class ProfileRepositoryImpl(
    private val api: WorkConnectApi
) : ProfileRepository {

    override suspend fun obtenerUsuarioPorCorreo(email: String): UserResponseDto? {
        return try {
            val emailLimpio = email.trim().lowercase()
            val response = api.getUsers()
            if (response.isSuccessful) {
                val usuarios = response.body()?.data?.items ?: emptyList()
                usuarios.firstOrNull { it.correo?.trim()?.lowercase() == emailLimpio }
            } else {
                Log.e("PROFILE_REPO", "Error al traer usuarios: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("PROFILE_REPO", "Excepción al buscar usuario: ${e.message}", e)
            null
        }
    }

    override suspend fun agregarRolAUsuario(
        userId: String,
        currentRoles: List<String>,
        newRole: String
    ): Boolean {
        return try {
            val rolesActualizados = (currentRoles + newRole).distinct()
            val response = api.updateUser(
                id = userId,
                request = UserUpdateRolesDto(roles = rolesActualizados)
            )
            if (response.isSuccessful) {
                Log.d("PROFILE_REPO", "Rol '$newRole' agregado al usuario $userId")
                true
            } else {
                Log.e("PROFILE_REPO", "Error al actualizar roles: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e("PROFILE_REPO", "Excepción al actualizar roles: ${e.message}", e)
            false
        }
    }

    override suspend fun actualizarDatosUsuario(
        userId: String,
        nombres: String,
        apellidos: String,
        telefono: String,
        roles: List<String>,
        empresaInfo: EmpresaInfoDto?
    ): Boolean {
        return try {
            val response = api.updateUserData(
                id = userId,
                request = UserUpdateDataDto(
                    nombres = nombres,
                    apellidos = apellidos,
                    telefono = telefono,
                    roles = roles,
                    empresaInfo = empresaInfo  // null si es solo candidato
                )
            )
            if (response.isSuccessful) {
                Log.d("PROFILE_REPO", "Datos actualizados del usuario $userId")
                true
            } else {
                Log.e("PROFILE_REPO", "Error al actualizar datos: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e("PROFILE_REPO", "Excepción al actualizar datos: ${e.message}", e)
            false
        }
    }
}

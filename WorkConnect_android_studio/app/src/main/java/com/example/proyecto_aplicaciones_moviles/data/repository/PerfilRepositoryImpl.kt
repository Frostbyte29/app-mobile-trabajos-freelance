package com.example.proyecto_aplicaciones_moviles.data.repository

import android.util.Log
import com.example.proyecto_aplicaciones_moviles.data.remote.EmpresaInfoDto
import com.example.proyecto_aplicaciones_moviles.data.remote.UsuarioResponseDto
import com.example.proyecto_aplicaciones_moviles.data.remote.UsuarioUpdateDataDto
import com.example.proyecto_aplicaciones_moviles.data.remote.UsuarioUpdateRolesDto
import com.example.proyecto_aplicaciones_moviles.data.remote.WorkConnectApi
import com.example.proyecto_aplicaciones_moviles.domain.model.EmpresaInfo
import com.example.proyecto_aplicaciones_moviles.domain.model.Usuario
import com.example.proyecto_aplicaciones_moviles.domain.repository.PerfilRepository

class PerfilRepositoryImpl(
    private val api: WorkConnectApi
) : PerfilRepository {

    private fun EmpresaInfo?.toDto(): EmpresaInfoDto? {
        this ?: return null
        return EmpresaInfoDto(
            nombre = nombre,
            rubro = rubro,
            correoContacto = correoContacto,
            telefono = telefono,
            sitioWeb = sitioWeb,
            direccion = direccion
        )
    }

    private fun UsuarioResponseDto.toDomain(): Usuario? {
        val id = id ?: return null
        return Usuario(
            id = id,
            nombres = nombres ?: "",
            apellidos = apellidos ?: "",
            correo = correo ?: "",
            telefono = telefono ?: "",
            fotoPerfilUrl = fotoPerfilUrl,
            acercaDe = acercaDe ?: "",
            roles = roles ?: emptyList(),
            empresaNombre = empresaInfo?.nombre ?: "",
            empresaRubro = empresaInfo?.rubro ?: "",
            empresaCorreoContacto = empresaInfo?.correoContacto ?: "",
            empresaTelefono = empresaInfo?.telefono ?: "",
            empresaSitioWeb = empresaInfo?.sitioWeb ?: "",
            empresaDireccion = empresaInfo?.direccion ?: ""
        )
    }

    override suspend fun obtenerUsuarioPorCorreo(email: String): Usuario? {
        return try {
            val emailLimpio = email.trim().lowercase()
            val response = api.getUsers()
            if (response.isSuccessful) {
                val usuarios = response.body()?.data?.items ?: emptyList()
                usuarios.firstOrNull { it.correo?.trim()?.lowercase() == emailLimpio }?.toDomain()
            } else {
                Log.e("PROFILE_REPO", "Error al traer usuarios: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("PROFILE_REPO", "Excepción al buscar usuario: ${e.message}", e)
            null
        }
    }

    override suspend fun obtenerUsuarioPorId(id: String): Usuario? {
        return try {
            val response = api.getUserById(id)
            if (response.isSuccessful) {
                response.body()?.data?.toDomain()
            } else {
                Log.e("PROFILE_REPO", "Error al obtener usuario por id: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("PROFILE_REPO", "Excepción al obtener usuario $id: ${e.message}", e)
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
                request = UsuarioUpdateRolesDto(roles = rolesActualizados)
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
        acercaDe: String?,
        empresaInfo: EmpresaInfo?
    ): Boolean {
        return try {
            val response = api.updateUserData(
                id = userId,
                request = UsuarioUpdateDataDto(
                    nombres = nombres,
                    apellidos = apellidos,
                    telefono = telefono,
                    acercaDe = acercaDe,
                    roles = roles,
                    empresaInfo = empresaInfo.toDto()
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
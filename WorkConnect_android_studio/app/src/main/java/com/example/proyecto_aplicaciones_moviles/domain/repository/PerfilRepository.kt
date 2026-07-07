package com.example.proyecto_aplicaciones_moviles.domain.repository

import com.example.proyecto_aplicaciones_moviles.domain.model.EmpresaInfo
import com.example.proyecto_aplicaciones_moviles.domain.model.Usuario

interface PerfilRepository {
    suspend fun obtenerUsuarioPorCorreo(email: String): Usuario?
    suspend fun obtenerUsuarioPorId(id: String): Usuario?
    suspend fun agregarRolAUsuario(userId: String, currentRoles: List<String>, newRole: String): Boolean
    suspend fun actualizarDatosUsuario(
        userId: String,
        nombres: String,
        apellidos: String,
        telefono: String,
        roles: List<String>,
        acercaDe: String? = null,
        empresaInfo: EmpresaInfo? = null
    ): Boolean
}

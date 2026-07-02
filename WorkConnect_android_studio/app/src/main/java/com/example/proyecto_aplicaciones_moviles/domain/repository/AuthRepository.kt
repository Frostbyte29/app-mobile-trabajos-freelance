package com.example.proyecto_aplicaciones_moviles.domain.repository

import com.example.proyecto_aplicaciones_moviles.data.remote.UserRequestDto

interface AuthRepository {
    suspend fun registrarCandidato(request: UserRequestDto): Boolean
    suspend fun verificarCorreoExiste(email: String): Boolean
}
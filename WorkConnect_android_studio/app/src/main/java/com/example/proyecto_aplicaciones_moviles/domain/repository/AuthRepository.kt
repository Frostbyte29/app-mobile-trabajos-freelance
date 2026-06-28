package com.example.proyecto_aplicaciones_moviles.domain.repository

import com.example.proyecto_aplicaciones_moviles.data.remote.UserRequestDto

interface AuthRepository {
    suspend fun registerCandidate(request: UserRequestDto): Boolean

    suspend fun verifyEmailExists(email: String): Boolean
}
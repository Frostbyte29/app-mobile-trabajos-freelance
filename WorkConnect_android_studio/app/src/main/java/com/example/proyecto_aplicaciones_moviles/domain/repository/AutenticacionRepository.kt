package com.example.proyecto_aplicaciones_moviles.domain.repository

import com.example.proyecto_aplicaciones_moviles.data.remote.UsuarioRequestDto

interface AutenticacionRepository {
    suspend fun registrarCandidato(request: UsuarioRequestDto): Boolean
    suspend fun verificarCorreoExiste(email: String): Boolean
}
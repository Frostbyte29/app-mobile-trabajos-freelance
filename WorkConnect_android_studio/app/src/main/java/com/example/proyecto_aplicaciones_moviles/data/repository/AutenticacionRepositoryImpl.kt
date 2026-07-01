package com.example.proyecto_aplicaciones_moviles.data.repository

import android.util.Log
import com.example.proyecto_aplicaciones_moviles.data.remote.UsuarioRequestDto
import com.example.proyecto_aplicaciones_moviles.data.remote.WorkConnectApi
import com.example.proyecto_aplicaciones_moviles.domain.repository.AutenticacionRepository

class AutenticacionRepositoryImpl(
    private val api: WorkConnectApi
) : AutenticacionRepository {

    override suspend fun registrarCandidato(request: UsuarioRequestDto): Boolean {
        return try {
            val response = api.registerCandidate(request)
            response.isSuccessful // Devuelve true si AWS responde exitosamente (Código 200-299)
        } catch (e: Exception) {
            Log.e("AWS_AUTH_ERROR", "Error al registrar candidato: ${e.message}", e)
            false
        }
    }

    override suspend fun verificarEmail(email: String): Boolean {
        return try {
            val emailLimpio = email.trim().lowercase()
            android.util.Log.d("AWS_LOGIN", "Intentando buscar el correo: '$emailLimpio'")

            val response = api.getUsers()

            if (response.isSuccessful) {
                // ¡LA MAGIA ESTÁ AQUÍ! Abrimos la caja "data" y sacamos los "items"
                val usuarios = response.body()?.data?.items ?: emptyList()

                android.util.Log.d("AWS_LOGIN", "AWS devolvió una lista con ${usuarios.size} usuarios")

                val existe = usuarios.any { it.correo?.trim()?.lowercase() == emailLimpio }

                android.util.Log.d("AWS_LOGIN", "¿Se encontró coincidencia?: $existe")
                existe
            } else {
                android.util.Log.e("AWS_LOGIN", "El servidor rechazó la petición. Código: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            android.util.Log.e("AWS_LOGIN", "Error al leer o procesar la lista: ${e.message}", e)
            false
        }
    }
}
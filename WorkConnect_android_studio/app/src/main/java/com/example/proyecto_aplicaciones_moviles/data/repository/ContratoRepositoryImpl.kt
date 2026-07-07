package com.example.proyecto_aplicaciones_moviles.data.repository

import android.util.Log
import com.example.proyecto_aplicaciones_moviles.data.remote.ContratoDto
import com.example.proyecto_aplicaciones_moviles.data.remote.ContratoEstadoDto
import com.example.proyecto_aplicaciones_moviles.data.remote.ContratoRequestDto
import com.example.proyecto_aplicaciones_moviles.data.remote.WorkConnectApi
import com.example.proyecto_aplicaciones_moviles.domain.model.Contrato
import com.example.proyecto_aplicaciones_moviles.domain.repository.ContratoRepository

class ContratoRepositoryImpl(
    private val api: WorkConnectApi
) : ContratoRepository {

    private fun ContratoDto.toDomain(): Contrato? {
        val id = id ?: return null
        return Contrato(
            id = id,
            contratanteId = contratanteId ?: "",
            freelancerId = freelancerId ?: "",
            ofertaId = ofertaId ?: "",
            tituloOferta = tituloOferta ?: "",
            tipoOrigen = tipoOrigen ?: "",
            estado = estado ?: "",
            fechaInicio = fechaInicio ?: "",
            fechaFin = fechaFin,
            postulacionId = postulacionId
        )
    }

    override suspend fun obtenerContratosComoFreelancer(freelancerId: String): List<Contrato> {
        return try {
            val response = api.getContratosPorFreelancer(freelancerId)
            if (!response.isSuccessful) return emptyList()
            response.body()?.data?.items?.mapNotNull { it.toDomain() } ?: emptyList()
        } catch (e: Exception) {
            Log.e("CONTRATO_REPO", "Excepción freelancer: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun obtenerContratosFinalizadosComoFreelancer(freelancerId: String): List<Contrato> {
        return obtenerContratosComoFreelancer(freelancerId).filter { it.estado == "finalizado" }
    }

    override suspend fun obtenerContratosComoContratante(contratanteId: String): List<Contrato> {
        return try {
            val response = api.getContratosPorContratante(contratanteId)
            if (!response.isSuccessful) return emptyList()
            response.body()?.data?.items?.mapNotNull { it.toDomain() } ?: emptyList()
        } catch (e: Exception) {
            Log.e("CONTRATO_REPO", "Excepción contratante: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun crearContrato(
        contratanteId: String,
        freelancerId: String,
        ofertaId: String,
        tituloOferta: String,
        tipoOrigen: String,
        postulacionId: String?
    ): Boolean {
        return try {
            val response = api.crearContrato(
                ContratoRequestDto(
                    contratanteId = contratanteId,
                    freelancerId = freelancerId,
                    ofertaId = ofertaId,
                    tituloOferta = tituloOferta,
                    tipoOrigen = tipoOrigen,
                    postulacionId = postulacionId
                )
            )
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("CONTRATO_REPO", "Excepción crear contrato: ${e.message}", e)
            false
        }
    }

    override suspend fun finalizarContrato(contratoId: String): Boolean {
        return try {
            val response = api.finalizarContrato(contratoId, ContratoEstadoDto(estado = "finalizado"))
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("CONTRATO_REPO", "Excepción finalizar contrato: ${e.message}", e)
            false
        }
    }
}

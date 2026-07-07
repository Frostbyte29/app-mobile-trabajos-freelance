package com.example.proyecto_aplicaciones_moviles.domain.repository

import com.example.proyecto_aplicaciones_moviles.domain.model.Contrato

interface ContratoRepository {
    suspend fun obtenerContratosComoFreelancer(freelancerId: String): List<Contrato>
    suspend fun obtenerContratosFinalizadosComoFreelancer(freelancerId: String): List<Contrato>
    suspend fun obtenerContratosComoContratante(contratanteId: String): List<Contrato>
    suspend fun crearContrato(
        contratanteId: String,
        freelancerId: String,
        ofertaId: String,
        tituloOferta: String,
        tipoOrigen: String,
        postulacionId: String? = null
    ): Boolean
    suspend fun finalizarContrato(contratoId: String): Boolean
}

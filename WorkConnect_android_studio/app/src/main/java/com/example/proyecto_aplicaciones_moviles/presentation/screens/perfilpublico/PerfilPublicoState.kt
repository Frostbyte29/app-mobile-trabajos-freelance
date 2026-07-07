package com.example.proyecto_aplicaciones_moviles.presentation.screens.perfilpublico

import com.example.proyecto_aplicaciones_moviles.domain.model.Contrato
import com.example.proyecto_aplicaciones_moviles.domain.model.Usuario
import com.example.proyecto_aplicaciones_moviles.domain.model.Valoracion

data class PerfilPublicoState(
    val isLoading: Boolean = false,
    val usuario: Usuario? = null,
    val valoraciones: List<Valoracion> = emptyList(),
    val trabajosRealizados: List<Contrato> = emptyList(),
    val errorMessage: String? = null,
    val isBuscandoChat: Boolean = false,
    val chatNavConvId: String? = null,
    val chatNavNombre: String? = null
)

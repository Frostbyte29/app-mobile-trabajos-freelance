package com.example.proyecto_aplicaciones_moviles.presentation.screens.detalle

import com.example.proyecto_aplicaciones_moviles.domain.model.Postulacion
import com.example.proyecto_aplicaciones_moviles.domain.model.Proyecto
import com.example.proyecto_aplicaciones_moviles.domain.model.Valoracion

data class DetalleProyectoState(
    val isLoading: Boolean = false,
    val proyecto: Proyecto? = null,
    val valoraciones: List<Valoracion> = emptyList(),
    val isLoadingValoraciones: Boolean = false,
    val isContratando: Boolean = false,
    val contratoExitoMsg: String? = null,
    val mostrarFormPostulacion: Boolean = false,
    val mensajePostulacion: String = "",
    val editLinkedinUrl: String = "",
    val editRepoUrl: String = "",
    val isPostulando: Boolean = false,
    val postulacionExitosa: Boolean = false,
    val errorMessage: String? = null,
    val postulaciones: List<Postulacion> = emptyList(),
    val isLoadingPostulantes: Boolean = false,
    val isBuscandoChat: Boolean = false,
    val chatNavConvId: String? = null,
    val chatNavNombre: String? = null
)
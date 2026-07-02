package com.example.proyecto_aplicaciones_moviles.presentation.screens.detail

import com.example.proyecto_aplicaciones_moviles.domain.model.Project
import com.example.proyecto_aplicaciones_moviles.domain.model.Valoracion

data class ProjectDetailState(
    val isLoading: Boolean = false,
    val project: Project? = null,

    // Valoraciones/comentarios sobre el publicador
    val valoraciones: List<Valoracion> = emptyList(),
    val isLoadingValoraciones: Boolean = false,

    // Formulario de nueva valoración
    val mostrarFormValoracion: Boolean = false,
    val puntuacionSeleccionada: Int = 5,
    val comentarioNuevo: String = "",
    val isEnviandoValoracion: Boolean = false,
    val valoracionEnviadaExito: Boolean = false,

    // Formulario de postulación
    val mostrarFormPostulacion: Boolean = false,
    val mensajePostulacion: String = "",
    val isPostulando: Boolean = false,
    val postulacionExitosa: Boolean = false,

    val errorMessage: String? = null
)

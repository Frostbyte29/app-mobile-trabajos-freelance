package com.example.proyecto_aplicaciones_moviles.presentation.screens.activity

import com.example.proyecto_aplicaciones_moviles.domain.model.Postulacion
import com.example.proyecto_aplicaciones_moviles.domain.model.Project

data class MyActivityState(

    val isLoading: Boolean = false,

    // Roles reales del usuario (cargados desde AWS)
    val roles: List<String> = emptyList(),

    // userId del usuario logueado (necesario para postularse y filtrar proyectos)
    val userId: String = "",

    // Postulaciones del candidato
    val postulaciones: List<Postulacion> = emptyList(),

    // Proyectos publicados por el reclutador
    val misProyectos: List<Project> = emptyList(),

    // Postulaciones recibidas en la oferta expandida (vista reclutador)
    val postulacionesOferta: List<Postulacion> = emptyList(),
    val ofertaExpandidaId: String? = null,
    val ofertaExpandidaTitulo: String = "",
    val isLoadingPostulaciones: Boolean = false,

    // Tab activo cuando tiene ambos roles: "candidato" o "reclutador"
    val tabActivo: String = "candidato",

    // true mientras se envía una postulación
    val isPostulando: Boolean = false,
    val postulacionExitosa: Boolean = false,

    // true mientras se cambia estado de postulación
    val isActualizandoEstado: Boolean = false,
    val estadoActualizadoExito: Boolean = false,

    val errorMessage: String? = null
)

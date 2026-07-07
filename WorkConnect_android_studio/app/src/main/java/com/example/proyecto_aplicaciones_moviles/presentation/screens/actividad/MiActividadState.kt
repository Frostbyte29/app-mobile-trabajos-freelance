package com.example.proyecto_aplicaciones_moviles.presentation.screens.actividad

import com.example.proyecto_aplicaciones_moviles.domain.model.Contrato
import com.example.proyecto_aplicaciones_moviles.domain.model.Postulacion
import com.example.proyecto_aplicaciones_moviles.domain.model.Proyecto
import com.example.proyecto_aplicaciones_moviles.domain.model.Valoracion

data class MiActividadState(

    val isLoading: Boolean = false,
    val roles: List<String> = emptyList(),
    val userId: String = "",
    val postulaciones: List<Postulacion> = emptyList(),
    val misProyectos: List<Proyecto> = emptyList(),
    val misServicios: List<Proyecto> = emptyList(),
    val postulacionesOferta: List<Postulacion> = emptyList(),
    val ofertaExpandidaId: String? = null,
    val ofertaExpandidaTitulo: String = "",
    val isLoadingPostulaciones: Boolean = false,
    val tabActivo: String = "candidato",
    val isPostulando: Boolean = false,
    val postulacionExitosa: Boolean = false,
    val isActualizandoEstado: Boolean = false,
    val estadoActualizadoExito: Boolean = false,
    val errorMessage: String? = null,
    val contratosFreelancer: List<Contrato> = emptyList(),
    val contratosContratante: List<Contrato> = emptyList(),
    val isLoadingContratos: Boolean = false,
    val isFinalizandoContrato: Boolean = false,
    val contratoFinalizadoExito: Boolean = false,
    val filtroContratosFreelancer: String = "todos",
    val filtroContratosContratante: String = "todos",
    val valoracionesEmitidas: List<Valoracion> = emptyList(),
    val contratoAValorarId: String? = null,
    val receptorIdAValorar: String? = null,
    val tituloContratoAValorar: String = "",
    val ofertaIdAValorar: String? = null,
    val valoracionIdAEditar: String? = null,
    val mostrarFormValoracion: Boolean = false,
    val puntuacionSeleccionada: Int = 5,
    val comentarioValoracion: String = "",
    val isEnviandoValoracion: Boolean = false,
    val valoracionEnviadaExito: Boolean = false,
    val isBuscandoChat: Boolean = false,
    val chatNavConvId: String? = null,
    val chatNavNombre: String? = null
)
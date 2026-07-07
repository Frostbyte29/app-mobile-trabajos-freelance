package com.example.proyecto_aplicaciones_moviles.core.utils

fun rolToDisplayName(rol: String): String = when (rol) {
    "candidato"  -> "Freelancer"
    "reclutador" -> "Contratante"
    else         -> rol
}

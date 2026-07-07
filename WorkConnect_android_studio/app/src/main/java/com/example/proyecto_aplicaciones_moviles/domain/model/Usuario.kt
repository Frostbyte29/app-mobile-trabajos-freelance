package com.example.proyecto_aplicaciones_moviles.domain.model

data class Usuario(
    val id: String,
    val nombres: String,
    val apellidos: String,
    val correo: String,
    val telefono: String,
    val fotoPerfilUrl: String?,
    val acercaDe: String,
    val roles: List<String>,
    val empresaNombre: String,
    val empresaRubro: String,
    val empresaCorreoContacto: String,
    val empresaTelefono: String,
    val empresaSitioWeb: String,
    val empresaDireccion: String
)

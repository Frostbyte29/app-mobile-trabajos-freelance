package com.example.proyecto_aplicaciones_moviles.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.proyecto_aplicaciones_moviles.data.repository.AutenticacionRepositoryImpl
import com.example.proyecto_aplicaciones_moviles.data.repository.ProyectoRepositoryImpl
import com.example.proyecto_aplicaciones_moviles.presentation.main.SharedProjectViewModel
import com.example.proyecto_aplicaciones_moviles.presentation.screens.autenticacion.AutenticacionViewModel

object AppContainer {

    // Repositorio de Proyectos
    val proyectoRepository by lazy {
        ProyectoRepositoryImpl(NetworkModule.workConnectApi)
    }

    // Repositorio de Usuarios (Auth)
    val autenticacionRepository by lazy {
        AutenticacionRepositoryImpl(NetworkModule.workConnectApi)
    }

    // FÁBRICA INTELIGENTE
    val CompartirViewModelFactory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when {
                // Si la pantalla pide el de Proyectos, le damos el de Proyectos
                modelClass.isAssignableFrom(SharedProjectViewModel::class.java) -> {
                    SharedProjectViewModel(proyectoRepository) as T
                }
                // Si la pantalla pide el de Auth, le damos el de Auth
                modelClass.isAssignableFrom(AutenticacionViewModel::class.java) -> {
                    AutenticacionViewModel(autenticacionRepository) as T
                }
                // Si pide algo que no existe, lanzamos un error claro
                else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
            }
        }
    }
}
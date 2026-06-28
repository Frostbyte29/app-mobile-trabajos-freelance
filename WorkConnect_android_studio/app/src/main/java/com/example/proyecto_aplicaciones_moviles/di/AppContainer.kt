package com.example.proyecto_aplicaciones_moviles.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.proyecto_aplicaciones_moviles.data.repository.AuthRepositoryImpl
import com.example.proyecto_aplicaciones_moviles.data.repository.ProjectRepositoryImpl
import com.example.proyecto_aplicaciones_moviles.presentation.main.SharedProjectViewModel
import com.example.proyecto_aplicaciones_moviles.presentation.screens.auth.AuthViewModel

object AppContainer {

    // Repositorio de Proyectos
    val projectRepository by lazy {
        ProjectRepositoryImpl(NetworkModule.workConnectApi)
    }

    // Repositorio de Usuarios (Auth)
    val authRepository by lazy {
        AuthRepositoryImpl(NetworkModule.workConnectApi)
    }

    // FÁBRICA INTELIGENTE
    val SharedViewModelFactory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when {
                // Si la pantalla pide el de Proyectos, le damos el de Proyectos
                modelClass.isAssignableFrom(SharedProjectViewModel::class.java) -> {
                    SharedProjectViewModel(projectRepository) as T
                }
                // Si la pantalla pide el de Auth, le damos el de Auth
                modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                    AuthViewModel(authRepository) as T
                }
                // Si pide algo que no existe, lanzamos un error claro
                else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
            }
        }
    }
}
package com.example.proyecto_aplicaciones_moviles.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.proyecto_aplicaciones_moviles.data.repository.ProjectRepositoryImpl
import com.example.proyecto_aplicaciones_moviles.presentation.main.SharedProjectViewModel

object AppContainer {

    // 1. Construimos el repositorio usando el API que conectamos antes
    private val projectRepository by lazy {
        ProjectRepositoryImpl(NetworkModule.workConnectApi)
    }

    // 2. Creamos la "Fábrica" que le inyecta ese repositorio al ViewModel
    val SharedViewModelFactory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SharedProjectViewModel(projectRepository) as T
        }
    }
}
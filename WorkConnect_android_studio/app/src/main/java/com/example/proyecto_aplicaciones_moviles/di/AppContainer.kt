package com.example.proyecto_aplicaciones_moviles.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.proyecto_aplicaciones_moviles.data.repository.AuthRepositoryImpl
import com.example.proyecto_aplicaciones_moviles.data.repository.NotificacionRepositoryImpl
import com.example.proyecto_aplicaciones_moviles.data.repository.PostulacionRepositoryImpl
import com.example.proyecto_aplicaciones_moviles.data.repository.ProfileRepositoryImpl
import com.example.proyecto_aplicaciones_moviles.data.repository.ProjectRepositoryImpl
import com.example.proyecto_aplicaciones_moviles.data.repository.ValoracionRepositoryImpl
import com.example.proyecto_aplicaciones_moviles.presentation.main.SharedProjectViewModel
import com.example.proyecto_aplicaciones_moviles.presentation.screens.activity.MyActivityViewModel
import com.example.proyecto_aplicaciones_moviles.presentation.screens.auth.AuthViewModel
import com.example.proyecto_aplicaciones_moviles.presentation.screens.detail.ProjectDetailViewModel
import com.example.proyecto_aplicaciones_moviles.presentation.screens.notifications.NotificacionViewModel
import com.example.proyecto_aplicaciones_moviles.presentation.screens.profile.ProfileViewModel

object AppContainer {

    val projectRepository by lazy { ProjectRepositoryImpl(NetworkModule.workConnectApi) }
    val authRepository by lazy { AuthRepositoryImpl(NetworkModule.workConnectApi) }
    val profileRepository by lazy { ProfileRepositoryImpl(NetworkModule.workConnectApi) }
    val postulacionRepository by lazy { PostulacionRepositoryImpl(NetworkModule.workConnectApi) }
    val valoracionRepository by lazy { ValoracionRepositoryImpl(NetworkModule.workConnectApi) }
    val notificacionRepository by lazy { NotificacionRepositoryImpl(NetworkModule.workConnectApi) }

    val SharedViewModelFactory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when {
                modelClass.isAssignableFrom(SharedProjectViewModel::class.java) ->
                    SharedProjectViewModel(projectRepository) as T
                modelClass.isAssignableFrom(AuthViewModel::class.java) ->
                    AuthViewModel(authRepository) as T
                modelClass.isAssignableFrom(ProfileViewModel::class.java) ->
                    ProfileViewModel(profileRepository) as T
                modelClass.isAssignableFrom(MyActivityViewModel::class.java) ->
                    MyActivityViewModel(postulacionRepository, profileRepository) as T
                modelClass.isAssignableFrom(ProjectDetailViewModel::class.java) ->
                    ProjectDetailViewModel(postulacionRepository, valoracionRepository) as T
                modelClass.isAssignableFrom(NotificacionViewModel::class.java) ->
                    NotificacionViewModel(notificacionRepository) as T
                else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
            }
        }
    }
}

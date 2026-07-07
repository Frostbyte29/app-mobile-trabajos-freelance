package com.example.proyecto_aplicaciones_moviles.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.proyecto_aplicaciones_moviles.data.repository.AutenticacionRepositoryImpl
import com.example.proyecto_aplicaciones_moviles.data.repository.NotificacionRepositoryImpl
import com.example.proyecto_aplicaciones_moviles.data.repository.PostulacionRepositoryImpl
import com.example.proyecto_aplicaciones_moviles.data.repository.PerfilRepositoryImpl
import com.example.proyecto_aplicaciones_moviles.data.repository.ProyectoRepositoryImpl
import com.example.proyecto_aplicaciones_moviles.data.repository.ValoracionRepositoryImpl
import com.example.proyecto_aplicaciones_moviles.data.repository.ChatRepositoryImpl
import com.example.proyecto_aplicaciones_moviles.data.repository.ContratoRepositoryImpl
import com.example.proyecto_aplicaciones_moviles.presentation.main.SharedProjectViewModel
import com.example.proyecto_aplicaciones_moviles.presentation.screens.actividad.MiActividadViewModel
import com.example.proyecto_aplicaciones_moviles.presentation.screens.auth.AutenticacionViewModel
import com.example.proyecto_aplicaciones_moviles.presentation.screens.detalle.DetalleProyectoViewModel
import com.example.proyecto_aplicaciones_moviles.presentation.screens.notificaciones.NotificacionViewModel
import com.example.proyecto_aplicaciones_moviles.presentation.screens.perfil.PerfilViewModel
import com.example.proyecto_aplicaciones_moviles.presentation.screens.mensajes.ChatViewModel
import com.example.proyecto_aplicaciones_moviles.presentation.screens.mensajes.ChatListViewModel
import com.example.proyecto_aplicaciones_moviles.presentation.screens.perfilpublico.PerfilPublicoViewModel

object AppContainer {

    val projectRepository by lazy { ProyectoRepositoryImpl(NetworkModule.workConnectApi) }
    val authRepository by lazy { AutenticacionRepositoryImpl(NetworkModule.workConnectApi) }
    val profileRepository by lazy { PerfilRepositoryImpl(NetworkModule.workConnectApi) }
    val postulacionRepository by lazy { PostulacionRepositoryImpl(NetworkModule.workConnectApi) }
    val valoracionRepository by lazy { ValoracionRepositoryImpl(NetworkModule.workConnectApi) }
    val notificacionRepository by lazy { NotificacionRepositoryImpl(NetworkModule.workConnectApi) }

    val contratoRepository by lazy { ContratoRepositoryImpl(NetworkModule.workConnectApi) }

    val chatRepository by lazy {
        ChatRepositoryImpl(NetworkModule.workConnectApi)
    }
    val SharedViewModelFactory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when {
                modelClass.isAssignableFrom(SharedProjectViewModel::class.java) ->
                    SharedProjectViewModel(projectRepository) as T
                modelClass.isAssignableFrom(AutenticacionViewModel::class.java) ->
                    AutenticacionViewModel(authRepository) as T
                modelClass.isAssignableFrom(PerfilViewModel::class.java) ->
                    PerfilViewModel(profileRepository) as T
                modelClass.isAssignableFrom(MiActividadViewModel::class.java) ->
                    MiActividadViewModel(postulacionRepository, profileRepository, chatRepository, contratoRepository, valoracionRepository) as T
                modelClass.isAssignableFrom(DetalleProyectoViewModel::class.java) ->
                    DetalleProyectoViewModel(postulacionRepository, valoracionRepository, contratoRepository, chatRepository) as T
                modelClass.isAssignableFrom(NotificacionViewModel::class.java) ->
                    NotificacionViewModel(notificacionRepository) as T
                modelClass.isAssignableFrom(ChatListViewModel::class.java) ->
                    ChatListViewModel(chatRepository) as T
                modelClass.isAssignableFrom(ChatViewModel::class.java) ->
                    ChatViewModel(chatRepository) as T
                modelClass.isAssignableFrom(PerfilPublicoViewModel::class.java) ->
                    PerfilPublicoViewModel(profileRepository, valoracionRepository, contratoRepository, chatRepository) as T
                else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
            }
        }
    }
}
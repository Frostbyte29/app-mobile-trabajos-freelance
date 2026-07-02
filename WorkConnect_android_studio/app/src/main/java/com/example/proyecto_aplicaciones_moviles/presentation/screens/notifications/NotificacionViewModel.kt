package com.example.proyecto_aplicaciones_moviles.presentation.screens.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_aplicaciones_moviles.core.utils.SessionManager
import com.example.proyecto_aplicaciones_moviles.domain.repository.NotificacionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotificacionViewModel(
    private val repository: NotificacionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(NotificacionState())
    val state: StateFlow<NotificacionState> = _state.asStateFlow()

    init {
        cargarNotificaciones()
    }

    fun cargarNotificaciones() {
        val userId = SessionManager.currentUserId ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val lista = repository.obtenerNotificaciones(userId)
            _state.update { it.copy(isLoading = false, notificaciones = lista) }
        }
    }

    fun marcarLeida(notificacionId: String) {
        viewModelScope.launch {
            repository.marcarLeida(notificacionId)
            // Actualiza localmente sin recargar la red
            _state.update { s ->
                s.copy(
                    notificaciones = s.notificaciones.map {
                        if (it.id == notificacionId) it.copy(leida = true) else it
                    }
                )
            }
        }
    }

    fun marcarTodasLeidas() {
        val noLeidas = _state.value.notificaciones.filter { !it.leida }
        noLeidas.forEach { marcarLeida(it.id) }
    }
}

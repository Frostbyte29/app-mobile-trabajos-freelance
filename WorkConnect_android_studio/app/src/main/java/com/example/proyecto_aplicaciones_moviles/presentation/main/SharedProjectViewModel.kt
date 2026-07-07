package com.example.proyecto_aplicaciones_moviles.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_aplicaciones_moviles.core.utils.SessionManager
import com.example.proyecto_aplicaciones_moviles.data.remote.ProyectoRequestDto
import com.example.proyecto_aplicaciones_moviles.domain.model.Proyecto
import com.example.proyecto_aplicaciones_moviles.domain.model.TipoOferta
import com.example.proyecto_aplicaciones_moviles.domain.repository.ProyectoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SharedProjectViewModel(
    private val repository: ProyectoRepository
) : ViewModel() {

    private val _allProjects = MutableStateFlow<List<Proyecto>>(emptyList())
    val allProjects: StateFlow<List<Proyecto>> = _allProjects.asStateFlow()

    private val _projects = MutableStateFlow<List<Proyecto>>(emptyList())
    val projects: StateFlow<List<Proyecto>> = _projects.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _categorias = MutableStateFlow<List<String>>(emptyList())
    val categorias: StateFlow<List<String>> = _categorias.asStateFlow()

    private val _filtroCategoria = MutableStateFlow("Todos")
    val filtroCategoria: StateFlow<String> = _filtroCategoria.asStateFlow()

    init {
        cargarProyectosDesdeAWS()
        cargarCategorias()
    }

    fun refrescarDatos() {
        cargarProyectosDesdeAWS()
    }

    fun refrescarSilencioso() {
        viewModelScope.launch {
            try {
                val remoteProjects = repository.obtenerProyectos()
                _allProjects.value = remoteProjects
                aplicarFiltro()
            } catch (_: Exception) {}
        }
    }

    private fun cargarProyectosDesdeAWS() {
        viewModelScope.launch {
            _isLoading.value = true
            val remoteProjects = repository.obtenerProyectos()
            _allProjects.value = remoteProjects
            aplicarFiltro()
            _isLoading.value = false
        }
    }

    private fun cargarCategorias() {
        viewModelScope.launch {
            _categorias.value = repository.obtenerCategorias()
        }
    }

    fun establecerFiltroCategoria(categoria: String) {
        _filtroCategoria.value = categoria
        aplicarFiltro()
    }

    fun refrescarFiltro() {
        aplicarFiltro()
    }

    private fun aplicarFiltro() {
        val filtroCategoria = _filtroCategoria.value
        val rolActivo = SessionManager.activeRole

        var lista = _allProjects.value

        lista = when (rolActivo) {
            "candidato" -> lista.filter {
                it.tipoOferta == TipoOferta.TRABAJO
            }
            "reclutador" -> lista.filter {
                it.tipoOferta == TipoOferta.SERVICIO
            }
            else -> lista
        }

        if (filtroCategoria != "Todos") {
            lista = lista.filter { it.category == filtroCategoria }
        }

        _projects.value = lista
    }

    suspend fun agregarProyecto(
        title: String,
        budget: String,
        category: String,
        description: String,
        empresa: String,
        tipoOferta: String,
        creadoPorId: String? = null
    ): Boolean {
        val budgetDouble = budget.toDoubleOrNull() ?: 0.0
        val request = ProyectoRequestDto(
            titulo = title,
            descripcion = description,
            presupuesto = budgetDouble,
            categoria = category,
            empresa = empresa,
            tipoOferta = tipoOferta,
            creadoPorId = creadoPorId
        )
        val success = repository.crearProyecto(request)
        if (success) cargarProyectosDesdeAWS()
        return success
    }
}
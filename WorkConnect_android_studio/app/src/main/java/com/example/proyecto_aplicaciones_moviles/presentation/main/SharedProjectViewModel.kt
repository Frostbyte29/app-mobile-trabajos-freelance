package com.example.proyecto_aplicaciones_moviles.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_aplicaciones_moviles.core.utils.SessionManager
import com.example.proyecto_aplicaciones_moviles.data.remote.ProjectRequestDto
import com.example.proyecto_aplicaciones_moviles.domain.model.Project
import com.example.proyecto_aplicaciones_moviles.domain.model.TipoOferta
import com.example.proyecto_aplicaciones_moviles.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SharedProjectViewModel(
    private val repository: ProjectRepository
) : ViewModel() {

    // Lista completa desde AWS
    private val _allProjects = MutableStateFlow<List<Project>>(emptyList())

    // Lista filtrada por tipo + categoría que ve la UI
    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects: StateFlow<List<Project>> = _projects.asStateFlow()

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

    // Re-aplica el filtro cuando cambia el rol activo (llamar desde ProfileViewModel al cambiar rol)
    fun refrescarFiltro() {
        aplicarFiltro()
    }

    private fun aplicarFiltro() {
        val filtroCategoria = _filtroCategoria.value
        val rolActivo = SessionManager.activeRole

        var lista = _allProjects.value

        // Lógica de filtro por tipo de oferta:
        // - candidato: ve "trabajo" + datos viejos sin tipoOferta (asumidos trabajo)
        // - reclutador: ve "servicio" (freelancers que ofrecen sus habilidades)
        // - invitado: ve todo
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
        val request = ProjectRequestDto(
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

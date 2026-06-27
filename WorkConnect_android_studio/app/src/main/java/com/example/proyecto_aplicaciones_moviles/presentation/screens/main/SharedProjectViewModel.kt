package com.example.proyecto_aplicaciones_moviles.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_aplicaciones_moviles.domain.model.Project
import com.example.proyecto_aplicaciones_moviles.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SharedProjectViewModel(
    private val repository: ProjectRepository // Inyectamos la conexión a datos
) : ViewModel() {

    // Lista vacía al inicio
    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects: StateFlow<List<Project>> = _projects.asStateFlow()

    // Estado para la animación de carga mientras esperamos a AWS
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        // Apenas nace el ViewModel, vamos a buscar los datos a la nube
        fetchProjectsFromAWS()
    }

    private fun fetchProjectsFromAWS() {
        viewModelScope.launch {
            _isLoading.value = true
            // ¡Aquí ocurre la magia! Llamamos a tu Lambda/DynamoDB
            val remoteProjects = repository.getProjects()
            _projects.value = remoteProjects
            _isLoading.value = false
        }
    }


    suspend fun addProject(title: String, budget: String, category: String, description: String): Boolean {
        // Convertimos el presupuesto de texto a número (Double)
        val budgetDouble = budget.toDoubleOrNull() ?: 0.0

        // 1. Armamos el paquete con los datos de la pantalla
        val request = com.example.proyecto_aplicaciones_moviles.data.remote.ProjectRequestDto(
            titulo = title,
            descripcion = description,
            presupuesto = budgetDouble,
            categoria = category,
            empresa = "Mi Empresa" // Lo dejamos fijo por ahora como en el Postman
        )

        // 2. Lo enviamos a AWS
        val success = repository.createProject(request)

        // 3. Si se guardó con éxito, actualizamos nuestra lista silenciosamente
        if (success) {
            fetchProjectsFromAWS()
        }

        return success
    }
}
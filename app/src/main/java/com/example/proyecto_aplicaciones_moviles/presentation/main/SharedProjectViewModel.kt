package com.example.proyecto_aplicaciones_moviles.presentation.main

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// 1. Definimos cómo es un "Proyecto"
data class Project(
    val title: String,
    val price: String,
    val priceType: String,
    val company: String,
    val timeAgo: String,
    val description: String,
    val tags: List<String>,
    val badgeText: String? = null,
    val isPrimaryAction: Boolean
)

class SharedProjectViewModel : ViewModel() {

    // 2. Lista inicial con los datos de prueba que ya tenías
    private val initialProjects = listOf(
        Project(
            title = "Estrategia de Implementación de IA Empresarial",
            price = "S/.2,500",
            priceType = "Precio Fijo",
            company = "TechCorp Global",
            timeAgo = "Publicado hace 2h",
            description = "Buscamos un estratega senior de IA para liderar el diseño y la hoja de ruta de una integración de LLM a nivel empresarial...",
            tags = listOf("Machine Learning", "Estrategia", "Python"),
            badgeText = "Entrega Urgente",
            isPrimaryAction = true
        ),
        Project(
            title = "Diseño UI/UX para Fintech",
            price = "S/.850",
            priceType = "6 meses",
            company = "Est. 20 h/semana",
            timeAgo = "",
            description = "Buscamos un diseñador detallista para renovar nuestro panel de trading móvil y...",
            tags = listOf("Figma", "Fintech"),
            isPrimaryAction = false
        )
    )

    // Estado reactivo que contiene la lista de proyectos
    private val _projects = MutableStateFlow(initialProjects)
    val projects: StateFlow<List<Project>> = _projects.asStateFlow()

    // 3. Función para simular la publicación de un nuevo proyecto
    fun addProject(title: String, budget: String, category: String, description: String) {
        val newProject = Project(
            title = title,
            price = "S/.$budget", // Le damos formato de moneda
            priceType = "Precio Fijo",
            company = "Mi Empresa", // Simula ser el usuario actual
            timeAgo = "Justo ahora",
            description = description,
            tags = listOf(category),
            badgeText = "Nuevo", // Le ponemos una etiqueta para que resalte
            isPrimaryAction = true
        )

        // Agregamos el proyecto nuevo AL INICIO de la lista
        _projects.update { currentList ->
            listOf(newProject) + currentList
        }
    }
}
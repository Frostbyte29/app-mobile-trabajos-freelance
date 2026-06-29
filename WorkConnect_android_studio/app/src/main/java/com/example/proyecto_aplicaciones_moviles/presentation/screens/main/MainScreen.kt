package com.example.proyecto_aplicaciones_moviles.presentation.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
// 1. IMPORTANTE: Agregamos la importación del viewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.proyecto_aplicaciones_moviles.di.AppContainer
import com.example.proyecto_aplicaciones_moviles.presentation.main.SharedProjectViewModel
import com.example.proyecto_aplicaciones_moviles.presentation.screens.explore.ExploreScreen
import com.example.proyecto_aplicaciones_moviles.presentation.screens.home.HomeScreen
import com.example.proyecto_aplicaciones_moviles.presentation.screens.main.components.BottomNavItem
import com.example.proyecto_aplicaciones_moviles.presentation.screens.main.components.WorkConnectBottomBar
// 2. Importamos la pantalla de publicar que vas a crear
import com.example.proyecto_aplicaciones_moviles.presentation.screens.publish.PublishScreen


@Composable
fun MainScreen(
    onNavigateToLogin: () -> Unit
) {
    val bottomNavController = rememberNavController()

    // 3. NUEVO: Creamos el "Cerebro" compartido que guardará los proyectos en memoria
    val sharedViewModel: SharedProjectViewModel = viewModel(factory = AppContainer.SharedViewModelFactory)

    Scaffold(
        bottomBar = {
            WorkConnectBottomBar(navController = bottomNavController)
        }
    ) { paddingValues ->

        NavHost(
            navController = bottomNavController,
            startDestination = BottomNavItem.Inicio.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(BottomNavItem.Inicio.route) {
                // 4. Conectamos la pantalla de inicio al cerebro
                HomeScreen(viewModel = sharedViewModel,
                    onNavigateToLogin=onNavigateToLogin)
            }
            composable(BottomNavItem.Explorar.route) {
                ExploreScreen()
            }
            composable(BottomNavItem.Publicar.route) {
                PublishScreen(viewModel = sharedViewModel)
            }
            composable(BottomNavItem.Mensajes.route) {
                PlaceholderScreen("Bandeja de Mensajes")
            }
            composable(BottomNavItem.Perfil.route) {
                PlaceholderScreen("Mi Perfil")
            }
        }
    }
}

// Un componente temporal para mostrar texto centrado en cada pestaña
@Composable
fun PlaceholderScreen(texto: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = texto, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}
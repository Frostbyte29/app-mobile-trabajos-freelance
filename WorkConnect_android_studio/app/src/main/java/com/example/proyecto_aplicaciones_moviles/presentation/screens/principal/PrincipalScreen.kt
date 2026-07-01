package com.example.proyecto_aplicaciones_moviles.presentation.screens.principal

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
import com.example.proyecto_aplicaciones_moviles.presentation.screens.explorar.ExplorarScreen
import com.example.proyecto_aplicaciones_moviles.presentation.screens.inicio.InicioScreen
import com.example.proyecto_aplicaciones_moviles.presentation.screens.principal.components.BottomNavItem
import com.example.proyecto_aplicaciones_moviles.presentation.screens.principal.components.WorkConnectBottomBar
// 2. Importamos la pantalla de publicar que vas a crear
import com.example.proyecto_aplicaciones_moviles.presentation.screens.publicar.PublicarScreen


@Composable
fun PrincipalScreen(
    onNavigateToLogin: () -> Unit
) {
    val bottomNavController = rememberNavController()

    // 3. NUEVO: Creamos el "Cerebro" compartido que guardará los proyectos en memoria
    val sharedViewModel: SharedProjectViewModel = viewModel(factory = AppContainer.CompartirViewModelFactory)

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
                InicioScreen(viewModel = sharedViewModel,
                    onNavigateToLogin=onNavigateToLogin)
            }
            composable(BottomNavItem.Explorar.route) {
                ExplorarScreen()
            }
            composable(BottomNavItem.Publicar.route) {
                PublicarScreen(viewModel = sharedViewModel)
            }
            composable(BottomNavItem.Mensajes.route) {
                MarcadorPosicionScreen("Bandeja de Mensajes")
            }
            composable(BottomNavItem.Perfil.route) {
                MarcadorPosicionScreen("Mi Perfil")
            }
        }
    }
}

// Un componente temporal para mostrar texto centrado en cada pestaña
@Composable
fun MarcadorPosicionScreen(texto: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = texto, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}
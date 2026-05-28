package com.example.proyecto_aplicaciones_moviles.presentation.main

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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.proyecto_aplicaciones_moviles.presentation.main.components.BottomNavItem
import com.example.proyecto_aplicaciones_moviles.presentation.main.components.WorkConnectBottomBar


@Composable
fun MainScreen() {
    // 1. Creamos un controlador de navegación interno SOLO para estas 5 pestañas
    val bottomNavController = rememberNavController()

    // 2. Scaffold nos permite colocar el BottomBar fijado en la parte inferior
    Scaffold(
        bottomBar = {
            WorkConnectBottomBar(navController = bottomNavController)
        }
    ) { paddingValues ->

        // 3. El NavHost interno que cambiará el contenido central
        NavHost(
            navController = bottomNavController,
            startDestination = BottomNavItem.Inicio.route,
            modifier = Modifier.padding(paddingValues) // Respeta el espacio de la barra inferior
        ) {
            composable(BottomNavItem.Inicio.route) {
                PlaceholderScreen("Pantalla de Inicio")
            }
            composable(BottomNavItem.Explorar.route) {
                PlaceholderScreen("Pantalla para Explorar Proyectos")
            }
            composable(BottomNavItem.Publicar.route) {
                PlaceholderScreen("Publicar un nuevo proyecto")
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
package com.example.proyecto_aplicaciones_moviles.presentation.screens.principal.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

// 1. Definimos las rutas exclusivas del menú inferior
sealed class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String,
    val hasNews: Boolean = false // Determina si mostramos el puntito rojo
) {
    object Inicio : BottomNavItem("Inicio", Icons.Filled.GridView, "inicio_tab")
    object Explorar : BottomNavItem("Explorar", Icons.Filled.Search, "explorar_tab")
    object Publicar : BottomNavItem("Publicar", Icons.Filled.AddCircle, "publicar_tab")
    object Mensajes : BottomNavItem("Mensajes", Icons.Filled.MailOutline, "mensajes_tab", hasNews = true)
    object Perfil : BottomNavItem("Perfil", Icons.Filled.Person, "perfil_tab")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkConnectBottomBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Inicio,
        BottomNavItem.Explorar,
        BottomNavItem.Publicar,
        BottomNavItem.Mensajes,
        BottomNavItem.Perfil
    )

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        // Obtenemos la ruta actual para saber qué ícono pintar de azul
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    if (item.hasNews) {
                        BadgedBox(
                            badge = { Badge(containerColor = Color.Red) } // El puntito rojo
                        ) {
                            Icon(imageVector = item.icon, contentDescription = item.title)
                        }
                    } else {
                        Icon(imageVector = item.icon, contentDescription = item.title)
                    }
                },
                label = { Text(text = item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        // Evita que se abran múltiples copias de la misma pestaña
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) { saveState = true }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                // Colores personalizados según tu diseño
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF1A365D), // Azul oscuro
                    selectedTextColor = Color(0xFF1A365D),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color(0xFFF0F4FF) // Fondo azul clarito al seleccionar
                )
            )
        }
    }
}
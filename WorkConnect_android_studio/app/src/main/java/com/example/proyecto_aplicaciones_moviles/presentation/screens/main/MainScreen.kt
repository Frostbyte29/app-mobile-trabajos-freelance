package com.example.proyecto_aplicaciones_moviles.presentation.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.proyecto_aplicaciones_moviles.core.navigation.AppRoute
import com.example.proyecto_aplicaciones_moviles.di.AppContainer
import com.example.proyecto_aplicaciones_moviles.presentation.main.SharedProjectViewModel
import com.example.proyecto_aplicaciones_moviles.presentation.screens.activity.MyActivityScreen
import com.example.proyecto_aplicaciones_moviles.presentation.screens.activity.MyActivityViewModel
import com.example.proyecto_aplicaciones_moviles.presentation.screens.detail.ProjectDetailScreen
import com.example.proyecto_aplicaciones_moviles.presentation.screens.explore.ExploreScreen
import com.example.proyecto_aplicaciones_moviles.presentation.screens.home.HomeScreen
import com.example.proyecto_aplicaciones_moviles.presentation.screens.main.components.BottomNavItem
import com.example.proyecto_aplicaciones_moviles.presentation.screens.main.components.WorkConnectBottomBar
import com.example.proyecto_aplicaciones_moviles.presentation.screens.notifications.NotificacionScreen
import com.example.proyecto_aplicaciones_moviles.presentation.screens.notifications.NotificacionViewModel
import com.example.proyecto_aplicaciones_moviles.presentation.screens.profile.ProfileScreen
import com.example.proyecto_aplicaciones_moviles.presentation.screens.publish.PublishScreen

@Composable
fun MainScreen(
    onNavigateToLogin: () -> Unit
) {
    val mainNavController = rememberNavController()

    val sharedViewModel: SharedProjectViewModel = viewModel(factory = AppContainer.SharedViewModelFactory)
    val activityViewModel: MyActivityViewModel = viewModel(factory = AppContainer.SharedViewModelFactory)
    val notificacionViewModel: NotificacionViewModel = viewModel(factory = AppContainer.SharedViewModelFactory)

    NavHost(
        navController = mainNavController,
        startDestination = "tabs"
    ) {
        // PANTALLAS CON BOTTOM BAR
        composable("tabs") {
            TabsScreen(
                sharedViewModel = sharedViewModel,
                activityViewModel = activityViewModel,
                onNavigateToLogin = onNavigateToLogin,
                onNavigateToDetail = { projectId ->
                    mainNavController.navigate(AppRoute.ProjectDetail.createRoute(projectId))
                },
                onNavigateToNotificaciones = {
                    mainNavController.navigate(AppRoute.Notificaciones.route)
                }
            )
        }

        // DETALLE DE OFERTA — sin bottom bar
        composable(
            route = AppRoute.ProjectDetail.route,
            arguments = listOf(navArgument("projectId") { type = NavType.StringType })
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: ""
            val allProjects by sharedViewModel.projects.collectAsState()
            val project = allProjects.firstOrNull { it.id == projectId }
            val activityState by activityViewModel.state.collectAsState()
            val postulacionExistente = activityState.postulaciones.firstOrNull { it.vacanteId == projectId }
            val yaPostulado = postulacionExistente != null
            val estadoPostulacion = postulacionExistente?.estado ?: ""

            if (project != null) {
                ProjectDetailScreen(
                    project = project,
                    onBack = { mainNavController.popBackStack() },
                    yaPostulado = yaPostulado,
                    estadoPostulacion = estadoPostulacion
                )
            } else {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text(
                        "Oferta no encontrada",
                        fontSize = 16.sp,
                        color = androidx.compose.ui.graphics.Color.Gray
                    )
                }
            }
        }

        // PANTALLA DE NOTIFICACIONES — sin bottom bar
        composable(route = AppRoute.Notificaciones.route) {
            NotificacionScreen(
                viewModel = notificacionViewModel,
                onBack = { mainNavController.popBackStack() }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Pantalla con Bottom Bar (tabs)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun TabsScreen(
    sharedViewModel: SharedProjectViewModel,
    activityViewModel: MyActivityViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToNotificaciones: () -> Unit
) {
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = { WorkConnectBottomBar(navController = bottomNavController) }
    ) { paddingValues ->
        NavHost(
            navController = bottomNavController,
            startDestination = BottomNavItem.Inicio.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(BottomNavItem.Inicio.route) {
                HomeScreen(
                    viewModel = sharedViewModel,
                    activityViewModel = activityViewModel,
                    onNavigateToLogin = onNavigateToLogin,
                    onNavigateToDetail = onNavigateToDetail,
                    onNavigateToNotificaciones = onNavigateToNotificaciones
                )
            }
            composable(BottomNavItem.Explorar.route) {
                ExploreScreen()
            }
            composable(BottomNavItem.Publicar.route) {
                PublishScreen(viewModel = sharedViewModel)
            }
            composable(BottomNavItem.Mensajes.route) {
                MyActivityScreen(
                    viewModel = activityViewModel,
                    onNavigateToDetail = onNavigateToDetail
                )
            }
            composable(BottomNavItem.Perfil.route) {
                ProfileScreen(onLogout = onNavigateToLogin)
            }
        }
    }
}

@Composable
fun PlaceholderScreen(texto: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = texto, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}

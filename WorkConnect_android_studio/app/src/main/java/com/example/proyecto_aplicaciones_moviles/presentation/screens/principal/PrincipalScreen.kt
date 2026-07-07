package com.example.proyecto_aplicaciones_moviles.presentation.screens.principal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.proyecto_aplicaciones_moviles.core.navigation.popBackStackSafely
import com.example.proyecto_aplicaciones_moviles.core.utils.SessionManager
import com.example.proyecto_aplicaciones_moviles.di.AppContainer
import com.example.proyecto_aplicaciones_moviles.presentation.main.SharedProjectViewModel
import com.example.proyecto_aplicaciones_moviles.presentation.screens.actividad.MiActividadScreen
import com.example.proyecto_aplicaciones_moviles.presentation.screens.actividad.MiActividadViewModel
import com.example.proyecto_aplicaciones_moviles.presentation.screens.detalle.DetalleProyectoScreen
import com.example.proyecto_aplicaciones_moviles.presentation.screens.explorar.ExplorarScreen
import com.example.proyecto_aplicaciones_moviles.presentation.screens.inicio.InicioScreen
import com.example.proyecto_aplicaciones_moviles.presentation.screens.main.components.BottomNavItem
import com.example.proyecto_aplicaciones_moviles.presentation.screens.main.components.WorkConnectBottomBar
import com.example.proyecto_aplicaciones_moviles.presentation.screens.mensajes.ChatListViewModel
import com.example.proyecto_aplicaciones_moviles.presentation.screens.mensajes.ChatScreen
import com.example.proyecto_aplicaciones_moviles.presentation.screens.mensajes.HistorialMensajesScreen
import com.example.proyecto_aplicaciones_moviles.presentation.screens.notificaciones.NotificacionScreen
import com.example.proyecto_aplicaciones_moviles.presentation.screens.notificaciones.NotificacionViewModel
import com.example.proyecto_aplicaciones_moviles.presentation.screens.perfil.PerfilScreen
import com.example.proyecto_aplicaciones_moviles.presentation.screens.perfilpublico.PerfilPublicoScreen
import com.example.proyecto_aplicaciones_moviles.presentation.screens.perfilpublico.PerfilPublicoViewModel
import com.example.proyecto_aplicaciones_moviles.presentation.screens.publicar.PublicarScreen

@Composable
fun PrincipalScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit = {},
    onSalir: () -> Unit = {}
) {
    val mainNavController = rememberNavController()

    val sharedViewModel: SharedProjectViewModel = viewModel(factory = AppContainer.SharedViewModelFactory)
    val activityViewModel: MiActividadViewModel = viewModel(factory = AppContainer.SharedViewModelFactory)
    val notificacionViewModel: NotificacionViewModel = viewModel(factory = AppContainer.SharedViewModelFactory)

    NavHost(
        navController = mainNavController,
        startDestination = "tabs"
    ) {
        composable("tabs") {
            PestañaScreen(
                sharedViewModel = sharedViewModel,
                activityViewModel = activityViewModel,
                onNavigateToLogin = onNavigateToLogin,
                onNavigateToRegister = onNavigateToRegister,
                onSalir = onSalir,
                onNavigateToDetail = { projectId ->
                    mainNavController.navigate(AppRoute.DetalleProyecto.createRoute(projectId))
                },
                onNavigateToNotificaciones = {
                    mainNavController.navigate(AppRoute.Notificaciones.route)
                },
                onNavigateToChat = { convId, nombre ->
                    mainNavController.navigate(AppRoute.Chat.createRoute(convId, nombre))
                },
                onNavigateToPerfilPublico = { usuarioId ->
                    mainNavController.navigate(AppRoute.PerfilPublico.createRoute(usuarioId))
                }
            )
        }

        composable(
            route = AppRoute.DetalleProyecto.route,
            arguments = listOf(navArgument("projectId") { type = NavType.StringType })
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: ""
            val allProjects by sharedViewModel.allProjects.collectAsState()
            val project = allProjects.firstOrNull { it.id == projectId }
            val activityState by activityViewModel.state.collectAsState()
            val postulacionExistente = activityState.postulaciones.firstOrNull { it.vacanteId == projectId }
            val yaPostulado = postulacionExistente != null
            val estadoPostulacion = postulacionExistente?.estado ?: ""

            if (project != null) {
                DetalleProyectoScreen(
                    proyecto = project,
                    onBack = { mainNavController.popBackStackSafely() },
                    yaPostulado = yaPostulado,
                    estadoPostulacion = estadoPostulacion,
                    onNavigateToChat = { convId, nombre ->
                        mainNavController.navigate(AppRoute.Chat.createRoute(convId, nombre))
                    },
                    onNavigateToLogin = onNavigateToLogin,
                    onNavigateToPerfilPublico = { usuarioId ->
                        mainNavController.navigate(AppRoute.PerfilPublico.createRoute(usuarioId))
                    }
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

        composable(route = AppRoute.Notificaciones.route) {
            NotificacionScreen(
                viewModel = notificacionViewModel,
                onBack = { mainNavController.popBackStackSafely() }
            )
        }

        composable(
            route = AppRoute.Chat.route,
            arguments = listOf(
                navArgument("conversationId") { type = NavType.StringType },
                navArgument("nombreOtroParticipante") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val convId = backStackEntry.arguments?.getString("conversationId") ?: ""
            val nombre = backStackEntry.arguments?.getString("nombreOtroParticipante") ?: ""
            ChatScreen(
                conversationId = convId,
                nombreOtroParticipante = nombre,
                onBack = { mainNavController.popBackStackSafely() }
            )
        }

        composable(
            route = AppRoute.PerfilPublico.route,
            arguments = listOf(navArgument("usuarioId") { type = NavType.StringType })
        ) { backStackEntry ->
            val usuarioId = backStackEntry.arguments?.getString("usuarioId") ?: ""
            val perfilPublicoViewModel: PerfilPublicoViewModel = viewModel(
                key = "perfilPublico_$usuarioId",
                factory = AppContainer.SharedViewModelFactory
            )
            PerfilPublicoScreen(
                usuarioId = usuarioId,
                viewModel = perfilPublicoViewModel,
                onBack = { mainNavController.popBackStackSafely() },
                onNavigateToChat = { convId, nombre ->
                    mainNavController.navigate(AppRoute.Chat.createRoute(convId, nombre))
                }
            )
        }
    }
}

@Composable
private fun PestañaScreen(
    sharedViewModel: SharedProjectViewModel,
    activityViewModel: MiActividadViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit = {},
    onSalir: () -> Unit = {},
    onNavigateToDetail: (String) -> Unit,
    onNavigateToNotificaciones: () -> Unit,
    onNavigateToChat: (String, String) -> Unit,
    onNavigateToPerfilPublico: (String) -> Unit
) {
    val bottomNavController = rememberNavController()
    val chatListViewModel: ChatListViewModel = viewModel(factory = AppContainer.SharedViewModelFactory)
    val chatListState by chatListViewModel.state.collectAsState()
    val userId = SessionManager.currentUserId
    LaunchedEffect(userId) {
        if (!userId.isNullOrBlank()) chatListViewModel.cargarConversaciones()
    }

    Scaffold(
        bottomBar = {
            WorkConnectBottomBar(
                navController = bottomNavController,
                hasUnreadMensajes = chatListState.hasUnreadMensajes
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = bottomNavController,
            startDestination = BottomNavItem.Inicio.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(BottomNavItem.Inicio.route) {
                InicioScreen(
                    viewModel = sharedViewModel,
                    activityViewModel = activityViewModel,
                    onNavigateToLogin = onNavigateToLogin,
                    onNavigateToDetail = onNavigateToDetail,
                    onNavigateToNotificaciones = onNavigateToNotificaciones,
                    onNavigateToPublicar = {
                        bottomNavController.navigate(BottomNavItem.Publicar.route) {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(BottomNavItem.Explorar.route) {
                ExplorarScreen()
            }
            composable(BottomNavItem.Publicar.route) {
                PublicarScreen(viewModel = sharedViewModel)
            }
            composable(BottomNavItem.Mensajes.route) {
                MiActividadScreen(
                    viewModel = activityViewModel,
                    onNavigateToDetail = onNavigateToDetail,
                    onNavigateToChat = onNavigateToChat,
                    onNavigateToPerfilPublico = onNavigateToPerfilPublico
                )
            }
            composable(BottomNavItem.Mensajes2.route) {
                HistorialMensajesScreen(
                    viewModel = chatListViewModel,
                    onNavigateToChat = onNavigateToChat
                )
            }
            composable(BottomNavItem.Perfil.route) {
                PerfilScreen(onLogout = onNavigateToLogin, onNavigateToRegister = onNavigateToRegister, onSalir = onSalir)
            }
        }
    }
}

@Composable
fun MarcadoPosicionScreen(texto: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = texto, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}
package com.example.proyecto_aplicaciones_moviles.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.proyecto_aplicaciones_moviles.presentation.screens.autenticacion.LoginScreen
import com.example.proyecto_aplicaciones_moviles.presentation.screens.autenticacion.RegistrarScreen
import com.example.proyecto_aplicaciones_moviles.presentation.screens.autenticacion.SplashScreen
import com.example.proyecto_aplicaciones_moviles.presentation.screens.principal.PrincipalScreen

@Composable
fun RootNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = AppRoute.AuthGraph.route,
        route = "root_graph"
    ) {

        navigation(
            startDestination = AppRoute.Splash.route,
            route = AppRoute.AuthGraph.route
        ) {
            composable(route = AppRoute.Splash.route) {
                SplashScreen(
                    onNavigateToLogin = { navController.navigate(AppRoute.Login.route) },
                    onNavigateToRegister = { navController.navigate(AppRoute.Registrar.route) }
                )
            }

            composable(route = AppRoute.Login.route) {
                LoginScreen(
                    onNavigateToRegister = {
                        navController.navigate(AppRoute.Registrar.route) {
                            popUpTo(AppRoute.Login.route) { inclusive = true }
                        }
                    },
                    onLoginSuccess = {
                        navController.navigate(AppRoute.InicioScreen.route) {
                            popUpTo(AppRoute.AuthGraph.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(route = AppRoute.Registrar.route) {
                RegistrarScreen(
                    onNavigateBack = { navController.popBackStackSafely() },
                    onRegisterSuccess = {
                        navController.navigate(AppRoute.Login.route) {
                            popUpTo(AppRoute.Splash.route)
                        }
                    }
                )
            }
        }

        composable(route = AppRoute.InicioScreen.route) {
            PrincipalScreen(
                onNavigateToLogin = {
                    navController.navigate(AppRoute.Login.route) {
                        popUpTo(AppRoute.InicioScreen.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(AppRoute.Registrar.route) {
                        popUpTo(AppRoute.InicioScreen.route) { inclusive = true }
                    }
                },
                onSalir = {
                    navController.navigate(AppRoute.Splash.route) {
                        popUpTo(AppRoute.InicioScreen.route) { inclusive = true }
                    }
                }
            )
        }
    }
}

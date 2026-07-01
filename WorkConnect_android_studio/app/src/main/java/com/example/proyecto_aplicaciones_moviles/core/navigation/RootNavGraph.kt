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
fun RootNavGraph(navController: NavHostController){
    NavHost(
        navController = navController,
        startDestination = AppRoute.AuthGraph.route, // 1. Arranca directamente en el grupo de Autenticación
        route = "root_graph"
    ) {

        // 2. MÓDULO DE AUTENTICACIÓN COMPLETO
        navigation(
            startDestination = AppRoute.Splash.route, // 3. La primera pantalla del grupo es el Splash
            route = AppRoute.AuthGraph.route
        ) {

            // PANTALLA DE SPLASH / BIENVENIDA
            composable(route = AppRoute.Splash.route) {
                SplashScreen(
                    onNavigateToLogin = {
                        navController.navigate(AppRoute.Login.route)
                        // Quitamos el popUpTo para que el Splash no se destruya y el usuario pueda regresar
                    },
                    onNavigateToRegister = {
                        navController.navigate(AppRoute.Register.route)
                        // Permitimos que vuelva atrás si se arrepiente
                    }
                )
            }

            // LOGIN
            composable(route = AppRoute.Login.route) {
                LoginScreen(
                    onNavigateToRegister = {
                        // Si está en Login y toca "Crear cuenta", lo mandamos a Registro
                        // pero cerramos el Login para no hacer un ciclo infinito (Login -> Registro -> Login -> Registro)
                        navController.navigate(AppRoute.Register.route) {
                            popUpTo(AppRoute.Login.route) { inclusive = true }
                        }
                    },
                    onLoginSuccess = {
                        // Login exitoso: Destruimos TODA la autenticación y pasamos al MainScreen
                        navController.navigate(AppRoute.MainScreen.route) {
                            popUpTo(AppRoute.AuthGraph.route) { inclusive = true }
                        }
                    }
                )
            }

            // REGISTRO
            composable(route = AppRoute.Register.route) {
                RegistrarScreen(
                    onNavigateBack = {
                        // Si se arrepiente, vuelve al Splash de forma natural
                        navController.popBackStack()
                    },
                    onRegisterSuccess = {
                        // Registro exitoso: Lo mandamos al Login
                        navController.navigate(AppRoute.Login.route) {
                            // Destruimos la pantalla de Registro del historial para que
                            // si presiona "Atrás" en el Login, no vuelva al formulario
                            popUpTo(AppRoute.Splash.route)
                        }
                    }
                )
            }
        }

        // 3. MÓDULO PRINCIPAL (HOME)
        composable(route = AppRoute.MainScreen.route) {
            PrincipalScreen(
                onNavigateToLogin = {
                    // Cuando el invitado toque "Iniciar Sesión" en el Home,
                    // lo mandamos a la ruta del Login y destruimos el MainScreen del historial
                    navController.navigate(AppRoute.Login.route) {
                        popUpTo(AppRoute.MainScreen.route) { inclusive = true }
                    }
                }
            )
        }
    }
}


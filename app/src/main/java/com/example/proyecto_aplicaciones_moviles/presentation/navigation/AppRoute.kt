package com.example.proyecto_aplicaciones_moviles.presentation.navigation

sealed class AppRoute(val route: String) {
    object Splash: AppRoute("splash_screen")
    object AuthGraph: AppRoute("auth_graph")
    object Login: AppRoute("login_screen")
    object Register: AppRoute("register_screen")
    object MainScreen: AppRoute("main_screen")
}

sealed class BottomNavRoute(val route: String){
    object Inicio: BottomNavRoute("inicio_tab")
    object Explorar: BottomNavRoute("explorar_tab")
    object Publicar: BottomNavRoute("publicar_tab")
    object Mensaje: BottomNavRoute("mensaje_tab")
    object Perfil: BottomNavRoute("perfil_tab")

}
package com.example.proyecto_aplicaciones_moviles.core.navigation

sealed class AppRoute(val route: String) {
    object Splash: AppRoute("splash_screen")
    object AuthGraph: AppRoute("auth_graph")
    object Login: AppRoute("login_screen")
    object Registrar: AppRoute("register_screen")
    object InicioScreen: AppRoute("main_screen")
    object DetalleProyecto: AppRoute("project_detail/{projectId}") {
        fun createRoute(projectId: String) = "project_detail/$projectId"
    }
    object Notificaciones: AppRoute("notificaciones_screen")

    object Chat : AppRoute("chat_screen/{conversationId}/{nombreOtroParticipante}") {
        fun createRoute(conversationId: String, nombreOtroParticipante: String) =
            "chat_screen/$conversationId/${android.net.Uri.encode(nombreOtroParticipante)}"
    }

    object PerfilPublico : AppRoute("perfil_publico/{usuarioId}") {
        fun createRoute(usuarioId: String) = "perfil_publico/$usuarioId"
    }
}
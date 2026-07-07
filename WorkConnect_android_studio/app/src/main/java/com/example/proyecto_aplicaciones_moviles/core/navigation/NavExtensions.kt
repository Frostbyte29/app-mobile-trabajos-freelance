package com.example.proyecto_aplicaciones_moviles.core.navigation

import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController

fun NavController.popBackStackSafely() {
    if (currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
        popBackStack()
    }
}

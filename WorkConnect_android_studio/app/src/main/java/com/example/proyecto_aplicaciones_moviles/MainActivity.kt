package com.example.proyecto_aplicaciones_moviles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.proyecto_aplicaciones_moviles.core.navigation.RootNavGraph
import com.example.proyecto_aplicaciones_moviles.ui.theme.Proyecto_Aplicaciones_MovilesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Proyecto_Aplicaciones_MovilesTheme{
                val navController= rememberNavController()

                RootNavGraph(navController=navController)
            }
        }
    }
}


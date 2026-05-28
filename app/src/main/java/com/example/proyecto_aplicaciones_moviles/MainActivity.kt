package com.example.proyecto_aplicaciones_moviles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.proyecto_aplicaciones_moviles.presentation.navigation.RootNavGraph
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


package com.example.proyecto_aplicaciones_moviles.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

@Composable
fun GuestPromptDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = {
                Text(text = "¡Únete a WorkConnect!", fontWeight = FontWeight.Bold, color = Color(0xFF1A365D))
            },
            text = {
                Text(text = "Para postular a este proyecto o publicar tus propias ofertas, necesitas iniciar sesión o crear una cuenta gratuita.")
            },
            confirmButton = {
                TextButton(onClick = {
                    onDismiss()
                    onNavigateToLogin()
                }) {
                    Text("Iniciar Sesión", color = Color(0xFF1A365D), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismiss() }) {
                    Text("Seguir mirando", color = Color.Gray)
                }
            }
        )
    }
}
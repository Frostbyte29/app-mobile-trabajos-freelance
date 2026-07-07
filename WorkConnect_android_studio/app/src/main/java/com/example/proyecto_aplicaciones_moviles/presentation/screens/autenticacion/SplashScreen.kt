package com.example.proyecto_aplicaciones_moviles.presentation.screens.autenticacion


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAddAlt1
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto_aplicaciones_moviles.presentation.components.WorkConnectButton

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Icon(
                imageVector = Icons.Filled.WorkOutline,
                contentDescription = "Logo",
                modifier = Modifier.size(100.dp),
                tint = Color(0xFF1A365D)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "WorkConnect",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A365D)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Conectando talento con oportunidades\nglobales",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            WorkConnectButton(
                text = "Quiero trabajar",
                onClick = { onNavigateToRegister() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { onNavigateToRegister() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Icon(
                    imageVector = Icons.Filled.PersonAddAlt1,
                    contentDescription = null,
                    tint = Color(0xFF1A365D),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Quiero contratar",
                    color = Color(0xFF1A365D),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "¿Ya tienes una cuenta? ", color = Color.Gray)
                Text(
                    text = "Iniciar sesión",
                    color = Color(0xFF1A365D),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }
        }
    }
}
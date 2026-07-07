package com.example.proyecto_aplicaciones_moviles.presentation.screens.explorar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ExplorarScreen() {
    val categories = listOf(
        ExploreCategory("Diseño y Creatividad", "Ilustradores, Diseñadores de Marca, Motion Artists", Icons.Filled.Brush),
        ExploreCategory("Programación y Tecnología", "Desarrollo Web, Apps, QA, Ciberseguridad", Icons.Filled.Code),
        ExploreCategory("Escritura y Traducción", "Redacción, Escritores, Editores", Icons.Filled.EditNote),
        ExploreCategory("Ventas y Marketing", "SEO, Ads, Redes Sociales", Icons.Filled.TrendingUp),
        ExploreCategory("Video y Animación", "Edición, Videos Explicativos", Icons.Filled.PlayCircleOutline),
        ExploreCategory("Administración y Soporte", "Asistentes Virtuales, Entrada de Datos", Icons.Filled.Headset),
        ExploreCategory("Finanzas y Legal", "Contadores, Abogados, Analistas", Icons.Filled.AccountBalance),
        ExploreCategory("Servicios de IA", "Entrenamiento de Modelos, Prompt Engineering", Icons.Filled.Psychology)
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item(span = { GridItemSpan(2) }) { Spacer(modifier = Modifier.height(16.dp)) }

        item(span = { GridItemSpan(2) }) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Filled.Person, contentDescription = "Avatar", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "WorkConnect", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A365D))
                }
                IconButton(onClick = { }) {
                    Icon(imageVector = Icons.Filled.NotificationsNone, contentDescription = "Notificaciones")
                }
            }
        }

        item(span = { GridItemSpan(2) }) {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                Text(
                    text = "Encuentra tu\npróximo proyecto",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1A365D),
                    lineHeight = 36.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Navega a través de las categorías freelance de mayor calidad para líderes de la industria y talento de élite.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    lineHeight = 20.sp
                )
            }
        }

        item(span = { GridItemSpan(2) }) {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Busca categorías o habilidades", color = Color.Gray, fontSize = 14.sp) },
                leadingIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = "Buscar", tint = Color.Gray) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color.LightGray
                ),
                singleLine = true
            )
        }

        item(span = { GridItemSpan(2) }) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Categorías en\nTendencia", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black, lineHeight = 26.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Ver\nTodo", fontSize = 14.sp, color = Color(0xFF1A365D), fontWeight = FontWeight.Bold)
                }
            }
        }

        item(span = { GridItemSpan(2) }) {
            TarjetaTendencia(
                title = "Diseño UI/UX y de\nProducto",
                subtitle = "Más de 3,420 vacantes activas",
                badge = "Destacado esta semana"
            )
        }
        item(span = { GridItemSpan(2) }) {
            TarjetaTendencia(
                title = "Ingeniería Full Stack",
                subtitle = "Más de 1,890 vacantes activas"
            )
        }

        item(span = { GridItemSpan(2) }) {
            Text(
                text = "Explorar Categorías",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
            )
        }

        items(categories) { category ->
            TarjetaCuadrículaCategoría(category = category)
        }

        item(span = { GridItemSpan(2) }) { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

data class ExploreCategory(val title: String, val description: String, val icon: ImageVector)

@Composable
fun TarjetaTendencia(title: String, subtitle: String, badge: String? = null) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF0F172A), Color(0xFF1E3A8A)) // Degradado Azul oscuro
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            if (badge != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF6EE7B7),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = badge,
                        color = Color(0xFF047857),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
            Text(text = title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White, lineHeight = 26.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = subtitle, fontSize = 12.sp, color = Color(0xFF94A3B8)) // Gris claro
        }
    }
}

@Composable
fun TarjetaCuadrículaCategoría(category: ExploreCategory) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFF0F4FF),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = category.icon, contentDescription = category.title, tint = Color(0xFF1A365D), modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = category.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), lineHeight = 18.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = category.description, fontSize = 11.sp, color = Color.Gray, lineHeight = 16.sp)
        }
    }
}
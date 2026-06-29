package com.example.proyecto_aplicaciones_moviles.presentation.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto_aplicaciones_moviles.core.utils.SessionManager
import com.example.proyecto_aplicaciones_moviles.presentation.components.GuestPromptDialog
import com.example.proyecto_aplicaciones_moviles.presentation.main.SharedProjectViewModel

@Composable
fun HomeScreen(
    viewModel: SharedProjectViewModel,
    onNavigateToLogin: () -> Unit // 1. Recibimos la ruta para mandar al usuario al Login
) {
    val projects by viewModel.projects.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // 2. EL INTERRUPTOR DEL DIÁLOGO DE INVITADO
    var showGuestDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // 3. ¡Protegemos el botón de crear proyecto!
                    if (SessionManager.isGuest) {
                        showGuestDialog = true
                    } else {
                        /* Acción real para ir a la pantalla de publicar */
                    }
                },
                containerColor = Color(0xFF1A365D),
                contentColor = Color.White,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Agregar")
            }
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // 1. BARRA SUPERIOR
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Filled.Person, contentDescription = "Avatar", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "WorkConnect",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A365D)
                        )
                    }
                    IconButton(onClick = { /* Notificaciones */ }) {
                        Icon(imageVector = Icons.Filled.NotificationsNone, contentDescription = "Notificaciones")
                    }
                }
            }

            // 2. BUSCADOR
            item {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("Buscar proyectos, habilidades o clientes...", color = Color.Gray, fontSize = 14.sp)
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.Search, contentDescription = "Buscar", tint = Color.Gray)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedBorderColor = Color.LightGray
                    ),
                    singleLine = true
                )
            }

            // 3. CHIPS DE FILTRO
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item { FilterChipCustom(text = "Categoría", isSelected = true) }
                    item { FilterChipCustom(text = "Presupuesto", isSelected = false) }
                    item { FilterChipCustom(text = "Plazo", isSelected = false) }
                }
            }

            // 4. TÍTULO DE SECCIÓN
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Recomendados para ti", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Ver todo", fontSize = 14.sp, color = Color(0xFF1A365D), fontWeight = FontWeight.Medium)
                }
            }

            // 5. LISTA DE TARJETAS DE AWS
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF1A365D))
                    }
                }
            } else {
                items(projects) { project ->
                    ProjectCard(
                        title = project.title,
                        price = "S/.${project.budget}",
                        priceType = "Precio Fijo",
                        company = project.company,
                        timeAgo = "Reciente",
                        description = project.description,
                        tags = listOf(project.category),
                        badgeText = "Nuevo",
                        isPrimaryAction = true,
                        // 4. ¡LA ACCIÓN DESDE LA TARJETA!
                        onApplyClick = {
                            if (SessionManager.isGuest) {
                                showGuestDialog = true
                            } else {
                                // Aquí irá la lógica de postularse real en el futuro
                            }
                        }
                    )
                }
            }

            // 6. TARJETA DE TALENTO
            item {
                TalentCard(
                    name = "Arturo Vance",
                    role = "Arquitecto Full-Stack Senior",
                    description = "Más de 12 años de experiencia escalando plataformas SaaS. Experta en React, Go y arquitectura de nube AWS."
                )
            }

            // 7. TARJETA MANUAL
            item {
                ProjectCard(
                    title = "Logo e Identidad de Marca",
                    price = "S/.800",
                    priceType = "Precio Fijo",
                    company = "5 días",
                    timeAgo = "",
                    description = "Crear una identidad de marca minimalista para una startup de moda sostenible,...",
                    tags = listOf("Branding", "Diseño de Logo"),
                    isPrimaryAction = false,
                    onApplyClick = {
                        if (SessionManager.isGuest) showGuestDialog = true
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }

        // 5. EL DIÁLOGO DE INVITADO (Aparecerá por encima de todo)
        GuestPromptDialog(
            showDialog = showGuestDialog,
            onDismiss = { showGuestDialog = false },
            onNavigateToLogin = {
                showGuestDialog = false
                onNavigateToLogin()
            }
        )
    }
}

// --- COMPONENTES REUTILIZABLES ---

@Composable
fun FilterChipCustom(text: String, isSelected: Boolean) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) Color(0xFF294485) else Color(0xFFE9ECEF),
        modifier = Modifier.height(32.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = text,
                color = if (isSelected) Color.White else Color.Black,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ProjectCard(
    title: String, price: String, priceType: String, company: String, timeAgo: String,
    description: String, tags: List<String>, badgeText: String? = null, isPrimaryAction: Boolean,
    onApplyClick: () -> Unit // 6. NUEVO PARÁMETRO: Le enseñamos a la tarjeta a recibir el clic
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (badgeText != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF6EE7B7).copy(alpha = 0.3f),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = badgeText,
                        color = Color(0xFF047857),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = price, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A365D))
                    Text(text = priceType, fontSize = 12.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            val subText = if (timeAgo.isNotEmpty()) "$company • $timeAgo" else company
            Text(text = subText, fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = description, fontSize = 13.sp, color = Color.DarkGray, lineHeight = 18.sp)
            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(tags.size) { index ->
                    Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFF3F4F6)) {
                        Text(
                            text = tags[index],
                            fontSize = 11.sp,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                if (isPrimaryAction) {
                    Button(
                        onClick = onApplyClick, // 7. LO CONECTAMOS AL BOTÓN DE POSTULARSE
                        modifier = Modifier.weight(1f).height(40.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF294485))
                    ) {
                        Text("Postularse", fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedIconButton(
                        onClick = { },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.BookmarkBorder, contentDescription = "Guardar", tint = Color(0xFF1A365D))
                    }
                } else {
                    OutlinedButton(
                        onClick = onApplyClick, // TAMBIÉN LO CONECTAMOS AQUÍ
                        modifier = Modifier.fillMaxWidth().height(40.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFF294485))
                    ) {
                        Text("Enviar Propuesta", color = Color(0xFF294485), fontSize = 14.sp)
                    }
                }
            }
        }
    }
}


@Composable
fun TalentCard(name: String, role: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0A1F44)) // Fondo azul oscuro
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Etiqueta Verde
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF047857),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text(
                    text = "TALENTO DESTACADO",
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            // Perfil
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Filled.Person, contentDescription = "Avatar", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(text = role, fontSize = 13.sp, color = Color(0xFFA0AEC0))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = description, fontSize = 13.sp, color = Color.White, lineHeight = 18.sp)

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text("Invitar al Proyecto", color = Color(0xFF0A1F44), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
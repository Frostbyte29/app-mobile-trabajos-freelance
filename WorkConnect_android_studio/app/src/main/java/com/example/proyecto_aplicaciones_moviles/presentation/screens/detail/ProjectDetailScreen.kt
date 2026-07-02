package com.example.proyecto_aplicaciones_moviles.presentation.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyecto_aplicaciones_moviles.core.utils.SessionManager
import com.example.proyecto_aplicaciones_moviles.di.AppContainer
import com.example.proyecto_aplicaciones_moviles.domain.model.Project
import com.example.proyecto_aplicaciones_moviles.domain.model.TipoOferta
import com.example.proyecto_aplicaciones_moviles.domain.model.Valoracion
import com.example.proyecto_aplicaciones_moviles.presentation.components.WorkConnectTextField
import kotlin.math.roundToInt

@Composable
fun ProjectDetailScreen(
    project: Project,
    onBack: () -> Unit,
    yaPostulado: Boolean = false,
    estadoPostulacion: String = "",   // estado de la postulación del usuario en esta oferta
    viewModel: ProjectDetailViewModel = viewModel(factory = AppContainer.SharedViewModelFactory)
) {
    val state by viewModel.state.collectAsState()

    // Carga el detalle al entrar
    LaunchedEffect(project.id) {
        viewModel.cargarDetalle(project)
    }

    // Limpia mensajes de éxito automáticamente
    LaunchedEffect(state.postulacionExitosa) {
        if (state.postulacionExitosa) {
            kotlinx.coroutines.delay(3000)
            viewModel.clearPostulacionExitosa()
        }
    }
    LaunchedEffect(state.valoracionEnviadaExito) {
        if (state.valoracionEnviadaExito) {
            kotlinx.coroutines.delay(3000)
            viewModel.clearValoracionExitosa()
        }
    }

    val esProyectoPropio = !SessionManager.currentUserId.isNullOrBlank() &&
        project.creadoPorId == SessionManager.currentUserId

    // Podemos postular si: es candidato, no es propio, no es invitado, y NO se ha postulado ya
    val puedePostular = !SessionManager.isGuest &&
        !esProyectoPropio &&
        !yaPostulado &&
        project.tipoOferta == TipoOferta.TRABAJO

    // Puede valorar SOLO si fue aceptado en esta oferta
    val puedeValorar = !SessionManager.isGuest &&
        !esProyectoPropio &&
        project.creadoPorId != null &&
        estadoPostulacion == "aceptado"

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
        ) {
            // ── BARRA SUPERIOR ────────────────────────────────────────────────
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Filled.ArrowBack, "Volver", tint = Color(0xFF1A365D))
                        }
                        Text(
                            text = "Detalle de la oferta",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A365D)
                        )
                    }
                }
            }

            // ── DATOS PRINCIPALES DE LA OFERTA ────────────────────────────────
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {

                        // Badge tipo oferta
                        val (badgeColor, badgeTextColor, badgeText, badgeIcon) = when (project.tipoOferta) {
                            TipoOferta.TRABAJO  -> listOf(
                                Color(0xFFDCFCE7), Color(0xFF166534), "Oferta de trabajo", Icons.Filled.Work
                            )
                            TipoOferta.SERVICIO -> listOf(
                                Color(0xFFE0E7FF), Color(0xFF4338CA), "Servicio freelance", Icons.Filled.BusinessCenter
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = badgeColor as Color
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = badgeIcon as androidx.compose.ui.graphics.vector.ImageVector,
                                    contentDescription = null,
                                    tint = badgeTextColor as Color,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    badgeText as String,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = badgeTextColor
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // Título
                        Text(
                            project.title,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A365D),
                            lineHeight = 32.sp
                        )
                        
                        Spacer(Modifier.height(12.dp))
                        
                        // Precio destacado
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF0F9FF)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AttachMoney,
                                    contentDescription = null,
                                    tint = Color(0xFF0369A1),
                                    modifier = Modifier.size(28.dp)
                                )
                                Column {
                                    Text(
                                        "S/. ${project.budget}",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF0369A1)
                                    )
                                    Text(
                                        "Presupuesto fijo",
                                        fontSize = 12.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(20.dp))
                        HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)
                        Spacer(Modifier.height(20.dp))

                        // Información del publicador mejorada
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Avatar del publicador
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(
                                        androidx.compose.ui.graphics.Brush.linearGradient(
                                            listOf(Color(0xFF1A365D), Color(0xFF2D4A7C))
                                        )
                                    )
                                    .border(3.dp, Color(0xFFE0E7FF), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    project.company.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp
                                )
                            }
                            
                            Spacer(Modifier.width(14.dp))
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    if (project.tipoOferta == TipoOferta.SERVICIO) "Ofrecido por" else "Publicado por",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    project.company,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1A365D)
                                )
                                // Fecha de publicación
                                if (project.createdAt.isNotBlank()) {
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        project.createdAt.formatFechaHoraDetalle(),
                                        fontSize = 12.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                                
                                // Calificación promedio si hay valoraciones
                                if (state.valoraciones.isNotEmpty()) {
                                    Spacer(Modifier.height(4.dp))
                                    val promedioCalificacion = state.valoraciones
                                        .map { it.puntuacion }
                                        .average()
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.Star,
                                            contentDescription = null,
                                            tint = Color(0xFFF59E0B),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            String.format("%.1f", promedioCalificacion),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF1A365D)
                                        )
                                        Text(
                                            " (${state.valoraciones.size} ${if (state.valoraciones.size == 1) "valoración" else "valoraciones"})",
                                            fontSize = 12.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }
                            }

                            // Icono verificado (opcional, decorativo)
                            if (!esProyectoPropio && project.creadoPorId != null) {
                                Icon(
                                    imageVector = Icons.Filled.VerifiedUser,
                                    contentDescription = "Verificado",
                                    tint = Color(0xFF059669),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        // Categoría
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Category,
                                contentDescription = null,
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(20.dp)
                            )
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF1F5F9)
                            ) {
                                Text(
                                    project.category,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF475569),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(20.dp))
                        HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)
                        Spacer(Modifier.height(20.dp))

                        // Descripción completa
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Description,
                                contentDescription = null,
                                tint = Color(0xFF1A365D),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                "Descripción del proyecto",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A365D)
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(
                            project.description,
                            fontSize = 15.sp,
                            color = Color(0xFF475569),
                            lineHeight = 24.sp
                        )
                    }
                }
            }

            // ── BANNERS DE ÉXITO ──────────────────────────────────────────────
            if (state.postulacionExitosa) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .shadow(2.dp, RoundedCornerShape(14.dp)),
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF10B981)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                            Column {
                                Text(
                                    "¡Postulación enviada!",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "El reclutador revisará tu perfil pronto",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }

            if (state.valoracionEnviadaExito) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .shadow(2.dp, RoundedCornerShape(14.dp)),
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF10B981)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                            Column {
                                Text(
                                    "¡Comentario publicado!",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Tu valoración ya es visible",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }

            // Error general
            if (state.errorMessage != null) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFEE2E2)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Warning,
                                contentDescription = null,
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                state.errorMessage!!,
                                color = Color(0xFF991B1B),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }

            // ── BOTÓN Y FORMULARIO DE POSTULACIÓN ─────────────────────────────
            // Banner "ya postulado" — visible en lugar del botón
            if (yaPostulado && project.tipoOferta == TipoOferta.TRABAJO && !esProyectoPropio) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFD1FAE5)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF059669),
                                modifier = Modifier.size(26.dp)
                            )
                            Column {
                                Text(
                                    "Ya te postulaste a esta oferta",
                                    color = Color(0xFF065F46),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Revisa el estado en la pestaña Actividad",
                                    color = Color(0xFF047857),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }

            if (puedePostular && !state.postulacionExitosa) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        if (!state.mostrarFormPostulacion) {
                            // Botón destacado para postularse
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF1A365D)
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                TextButton(
                                    onClick = { viewModel.abrirFormPostulacion() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(64.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Send,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(Modifier.width(12.dp))
                                        Text(
                                            "Postularme a esta oferta",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        } else {
                            // Formulario de postulación mejorado
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(6.dp, RoundedCornerShape(16.dp)),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(modifier = Modifier.padding(24.dp)) {
                                    // Encabezado del formulario
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFDCFCE7)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.RateReview,
                                                contentDescription = null,
                                                tint = Color(0xFF059669),
                                                modifier = Modifier.size(26.dp)
                                            )
                                        }
                                        Column {
                                            Text(
                                                "Enviar postulación",
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1A365D)
                                            )
                                            Text(
                                                "Cuéntale por qué eres ideal",
                                                fontSize = 13.sp,
                                                color = Color(0xFF64748B)
                                            )
                                        }
                                    }

                                    Spacer(Modifier.height(20.dp))

                                    // Campo de mensaje
                                    Text(
                                        "Mensaje de presentación",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF475569)
                                    )
                                    Text(
                                        "Opcional - Describe tu experiencia y por qué eres el candidato perfecto",
                                        fontSize = 12.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    
                                    OutlinedTextField(
                                        value = state.mensajePostulacion,
                                        onValueChange = viewModel::onMensajePostulacionChange,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(160.dp),
                                        placeholder = {
                                            Text(
                                                "Ejemplo: Hola! Tengo 5 años de experiencia en desarrollo móvil con Kotlin y he trabajado en proyectos similares...",
                                                color = Color(0xFFCBD5E1),
                                                fontSize = 14.sp
                                            )
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            unfocusedContainerColor = Color(0xFFF8FAFC),
                                            focusedContainerColor = Color.White,
                                            unfocusedBorderColor = Color(0xFFE2E8F0),
                                            focusedBorderColor = Color(0xFF1A365D)
                                        ),
                                        textStyle = LocalTextStyle.current.copy(
                                            fontSize = 14.sp,
                                            lineHeight = 20.sp
                                        )
                                    )

                                    Spacer(Modifier.height(8.dp))
                                    
                                    // Consejo
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color(0xFFF0F9FF)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Lightbulb,
                                                contentDescription = null,
                                                tint = Color(0xFF0369A1),
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Text(
                                                "Consejo: Menciona tu experiencia relevante y disponibilidad",
                                                fontSize = 12.sp,
                                                color = Color(0xFF0369A1),
                                                lineHeight = 16.sp
                                            )
                                        }
                                    }

                                    Spacer(Modifier.height(20.dp))

                                    // Botones de acción
                                    if (state.isPostulando) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(56.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(
                                                color = Color(0xFF1A365D),
                                                modifier = Modifier.size(36.dp)
                                            )
                                        }
                                    } else {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            OutlinedButton(
                                                onClick = viewModel::cerrarFormPostulacion,
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(56.dp),
                                                shape = RoundedCornerShape(12.dp),
                                                colors = ButtonDefaults.outlinedButtonColors(
                                                    contentColor = Color(0xFF64748B)
                                                ),
                                                border = BorderStroke(1.5.dp, Color(0xFFE2E8F0))
                                            ) {
                                                Text(
                                                    "Cancelar",
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                            Button(
                                                onClick = viewModel::enviarPostulacion,
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(56.dp),
                                                shape = RoundedCornerShape(12.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = Color(0xFF059669)
                                                ),
                                                elevation = ButtonDefaults.buttonElevation(
                                                    defaultElevation = 2.dp,
                                                    pressedElevation = 4.dp
                                                )
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Filled.Send,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                    Text(
                                                        "Enviar",
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(24.dp))
                    }
                }
            }

            // ── SECCIÓN DE VALORACIONES / COMENTARIOS ─────────────────────────
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Reviews,
                                    contentDescription = null,
                                    tint = Color(0xFF1A365D),
                                    modifier = Modifier.size(24.dp)
                                )
                                Column {
                                    Text(
                                        "Valoraciones",
                                        fontSize = 19.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1A365D)
                                    )
                                    if (state.valoraciones.isNotEmpty()) {
                                        val promedio = state.valoraciones
                                            .map { it.puntuacion }
                                            .average()
                                        Text(
                                            "${state.valoraciones.size} ${if (state.valoraciones.size == 1) "reseña" else "reseñas"} • ★ ${String.format("%.1f", promedio)}",
                                            fontSize = 12.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }
                            }
                            
                            if (puedeValorar && !state.mostrarFormValoracion) {
                                Button(
                                    onClick = { viewModel.abrirFormValoracion() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF1A365D)
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    elevation = ButtonDefaults.buttonElevation(
                                        defaultElevation = 0.dp
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Add,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        "Comentar",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            // Formulario de nueva valoración
            if (state.mostrarFormValoracion) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .shadow(6.dp, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            // Encabezado
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFEF3C7)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFF59E0B),
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        "Dejar una valoración",
                                        fontSize = 19.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1A365D)
                                    )
                                    Text(
                                        "Comparte tu experiencia",
                                        fontSize = 13.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }

                            Spacer(Modifier.height(20.dp))
                            HorizontalDivider(color = Color(0xFFE2E8F0))
                            Spacer(Modifier.height(20.dp))

                            // Selector de estrellas mejorado
                            Text(
                                "Calificación",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF475569)
                            )
                            Spacer(Modifier.height(10.dp))
                            
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFFEF3C7).copy(alpha = 0.3f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    (1..5).forEach { estrella ->
                                        IconButton(
                                            onClick = { viewModel.onPuntuacionChange(estrella) },
                                            modifier = Modifier.size(52.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (estrella <= state.puntuacionSeleccionada)
                                                    Icons.Filled.Star else Icons.Filled.StarBorder,
                                                contentDescription = "$estrella estrellas",
                                                tint = Color(0xFFF59E0B),
                                                modifier = Modifier.size(36.dp)
                                            )
                                        }
                                    }
                                }
                            }
                            
                            // Texto descriptivo de la calificación
                            Spacer(Modifier.height(8.dp))
                            val descripcionCalificacion = when (state.puntuacionSeleccionada) {
                                1 -> "Muy insatisfecho"
                                2 -> "Insatisfecho"
                                3 -> "Neutral"
                                4 -> "Satisfecho"
                                5 -> "Muy satisfecho"
                                else -> ""
                            }
                            Text(
                                descripcionCalificacion,
                                fontSize = 13.sp,
                                color = Color(0xFF64748B),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(Modifier.height(20.dp))

                            // Campo de comentario
                            Text(
                                "Tu comentario *",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF475569)
                            )
                            Text(
                                "Describe tu experiencia trabajando con este usuario",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8)
                            )
                            Spacer(Modifier.height(8.dp))
                            
                            OutlinedTextField(
                                value = state.comentarioNuevo,
                                onValueChange = viewModel::onComentarioChange,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp),
                                placeholder = {
                                    Text(
                                        "Escribe aquí tu experiencia...",
                                        color = Color(0xFFCBD5E1),
                                        fontSize = 14.sp
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = Color(0xFFF8FAFC),
                                    focusedContainerColor = Color.White,
                                    unfocusedBorderColor = Color(0xFFE2E8F0),
                                    focusedBorderColor = Color(0xFF1A365D)
                                ),
                                textStyle = LocalTextStyle.current.copy(
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp
                                )
                            )

                            if (state.errorMessage != null) {
                                Spacer(Modifier.height(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFFEE2E2)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Warning,
                                            contentDescription = null,
                                            tint = Color(0xFFDC2626),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            state.errorMessage!!,
                                            color = Color(0xFF991B1B),
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(20.dp))

                            // Botones de acción
                            if (state.isEnviandoValoracion) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = Color(0xFF1A365D),
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = viewModel::cerrarFormValoracion,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(56.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = Color(0xFF64748B)
                                        ),
                                        border = BorderStroke(1.5.dp, Color(0xFFE2E8F0))
                                    ) {
                                        Text(
                                            "Cancelar",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    Button(
                                        onClick = viewModel::enviarValoracion,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(56.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFF59E0B)
                                        ),
                                        elevation = ButtonDefaults.buttonElevation(
                                            defaultElevation = 2.dp,
                                            pressedElevation = 4.dp
                                        )
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Send,
                                                contentDescription = null,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Text(
                                                "Publicar",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }

            // Lista de valoraciones existentes
            if (state.isLoadingValoraciones) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFF1A365D),
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                "Cargando valoraciones...",
                                fontSize = 14.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }
            } else if (state.valoraciones.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE0E7FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.RateReview,
                                    contentDescription = null,
                                    tint = Color(0xFF6366F1),
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "Sin valoraciones aún",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A365D),
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "Sé el primero en dejar un comentario sobre este publicador",
                                fontSize = 14.sp,
                                color = Color(0xFF64748B),
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            } else {
                items(state.valoraciones) { valoracion ->
                    ValoracionCard(valoracion = valoracion)
                }
            }

            item { Spacer(Modifier.height(100.dp)) }
        }

        // Botón flotante de postulación (si aplica y no está el formulario abierto)
        if (puedePostular && !state.postulacionExitosa && !state.mostrarFormPostulacion) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 20.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                FloatingActionButton(
                    onClick = { viewModel.abrirFormPostulacion() },
                    containerColor = Color(0xFF1A365D),
                    contentColor = Color.White,
                    modifier = Modifier.size(width = 200.dp, height = 56.dp),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Send,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            "Postularme ahora",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// COMPONENTE: Tarjeta de valoración/comentario mejorada
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ValoracionCard(valoracion: Valoracion) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Cabecera: Usuario y calificación
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Información del usuario
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                androidx.compose.ui.graphics.Brush.linearGradient(
                                    listOf(Color(0xFF1A365D), Color(0xFF2D4A7C))
                                )
                            )
                            .border(2.dp, Color(0xFFE0E7FF), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            valoracion.nombreEmisor.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column {
                        Text(
                            valoracion.nombreEmisor.ifBlank { "Usuario anónimo" },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A365D)
                        )
                        Text(
                            valoracion.fechaCreacion.formatFechaHoraDetalle(),
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                // Calificación con estrellas
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFEF3C7)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        repeat(valoracion.puntuacion) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        repeat(5 - valoracion.puntuacion) {
                            Icon(
                                imageVector = Icons.Filled.StarBorder,
                                contentDescription = null,
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Comentario
            if (valoracion.comentario.isNotBlank()) {
                Spacer(Modifier.height(14.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF8FAFC)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.FormatQuote,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                valoracion.comentario,
                                fontSize = 14.sp,
                                color = Color(0xFF475569),
                                lineHeight = 21.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Indicador de proyecto (opcional, si tienes el ID de proyecto)
            if (valoracion.proyectoId?.isNotBlank() == true) {
                Spacer(Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Work,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        "Valoración por proyecto",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// Formatea ISO 8601 → "15 ene 2024 • 14:32"
private fun String.formatFechaHoraDetalle(): String {
    if (this.isBlank()) return ""
    return try {
        val limpio = this.replace("Z", "").replace("T", " ").take(16)
        val partes = limpio.split(" ")
        val fecha = partes[0]
        val hora  = partes.getOrNull(1) ?: ""
        val meses = listOf("","ene","feb","mar","abr","may","jun","jul","ago","sep","oct","nov","dic")
        val (anio, mes, dia) = fecha.split("-")
        val mesNombre = meses.getOrElse(mes.toIntOrNull() ?: 0) { mes }
        if (hora.isNotBlank()) "$dia $mesNombre $anio • $hora" else "$dia $mesNombre $anio"
    } catch (e: Exception) {
        this.take(10)
    }
}

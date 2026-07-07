package com.example.proyecto_aplicaciones_moviles.presentation.screens.mensajes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyecto_aplicaciones_moviles.di.AppContainer

@Composable
fun ChatScreen(
    conversationId: String,
    nombreOtroParticipante: String,
    onBack: () -> Unit
) {
    val viewModel: ChatViewModel = viewModel(factory = AppContainer.SharedViewModelFactory)
    val chatListViewModel: ChatListViewModel = viewModel(factory = AppContainer.SharedViewModelFactory)
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()

    var inputText by remember { mutableStateOf("") }

    LaunchedEffect(conversationId) {
        viewModel.cargarMensajes(conversationId, nombreOtroParticipante)
        chatListViewModel.marcarLeida(conversationId)
        while (true) {
            kotlinx.coroutines.delay(5_000)
            viewModel.actualizarMensajesSilencioso(conversationId)
        }
    }

    LaunchedEffect(state.mensajes.size) {
        if (state.mensajes.isNotEmpty()) {
            listState.animateScrollToItem(state.mensajes.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .imePadding()
            .navigationBarsPadding()
    ) {
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
                    text = state.nombreOtroParticipante.ifBlank { nombreOtroParticipante },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A365D),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF1A365D))
                }
            }
            state.errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.errorMessage!!, color = MaterialTheme.colorScheme.error)
                }
            }
            state.mensajes.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Aún no hay mensajes. ¡Sé el primero en escribir!", color = Color.Gray)
                }
            }
            else -> {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.mensajes) { mensaje ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (mensaje.esMio) Arrangement.End else Arrangement.Start
                        ) {
                            Surface(
                                shape = RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = if (mensaje.esMio) 16.dp else 4.dp,
                                    bottomEnd = if (mensaje.esMio) 4.dp else 16.dp
                                ),
                                color = if (mensaje.esMio) Color(0xFF1A365D) else Color(0xFFF0F4FF),
                                modifier = Modifier.widthIn(max = 280.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(
                                        horizontal = 14.dp,
                                        vertical = 10.dp
                                    )
                                ) {
                                    Text(
                                        text = mensaje.contenido,
                                        color = if (mensaje.esMio) Color.White else Color(0xFF1A365D),
                                        fontSize = 14.sp,
                                        lineHeight = 20.sp
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = mensaje.fechaEnvio.take(16).replace("T", " "),
                                        fontSize = 10.sp,
                                        color = if (mensaje.esMio) Color.White.copy(alpha = 0.7f) else Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Escribe un mensaje...", color = Color.Gray) },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE5E7EB),
                        focusedBorderColor = Color(0xFF1A365D)
                    ),
                    maxLines = 4
                )
                Spacer(Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        val texto = inputText
                        inputText = ""
                        viewModel.enviarMensaje(conversationId, texto)
                    },
                    enabled = inputText.isNotBlank() && !state.isEnviando
                ) {
                    Icon(
                        imageVector = Icons.Filled.Send,
                        contentDescription = "Enviar",
                        tint = if (inputText.isNotBlank() && !state.isEnviando)
                            Color(0xFF1A365D) else Color.LightGray
                    )
                }
            }
        }
    }
}

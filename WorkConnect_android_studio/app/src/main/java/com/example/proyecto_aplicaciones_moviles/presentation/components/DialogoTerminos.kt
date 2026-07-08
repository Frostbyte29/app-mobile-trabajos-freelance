package com.example.proyecto_aplicaciones_moviles.presentacion.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun DialogoTerminos(
    mostrarDialogo: Boolean,
    alCerrar: () -> Unit,
    alAceptar: () -> Unit // Opcional: si quieres que el botón "Aceptar" marque la casilla automáticamente
) {
    if (mostrarDialogo) {
        Dialog(
            onDismissRequest = alCerrar,
            properties = DialogProperties(usePlatformDefaultWidth = false) // Para que ocupe un buen espacio
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f) // Ocupa el 90% del ancho de la pantalla
                    .fillMaxHeight(0.8f), // Ocupa el 80% del alto
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // 1. CABECERA
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1A365D))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Términos y Condiciones",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // 2. CUERPO DE TEXTO CON SCROLL
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f) // Toma el espacio restante
                            .padding(20.dp)
                            .verticalScroll(scrollState)
                    ) {
                        Text(
                            text = "Bienvenido a WorkConnect.\n\n" +
                                    "Al utilizar nuestra aplicación, aceptas estar sujeto a los siguientes términos y condiciones. Por favor, léelos cuidadosamente.\n\n" +
                                    "1. Uso de la Cuenta\n" +
                                    "Eres responsable de mantener la confidencialidad de tu cuenta y contraseña. WorkConnect no se hace responsable por accesos no autorizados.\n\n" +
                                    "2. Publicación de Proyectos\n" +
                                    "Todo contenido publicado debe ser legal, honesto y no violar los derechos de terceros. Nos reservamos el derecho de eliminar ofertas fraudulentas.\n\n" +
                                    "3. Postulaciones\n" +
                                    "La plataforma actúa como intermediario. Los acuerdos, pagos y contratos finales son responsabilidad exclusiva entre el reclutador y el candidato.\n\n" +
                                    "4. Privacidad\n" +
                                    "Tus datos serán tratados conforme a nuestra Política de Privacidad, asegurando que tu correo e información personal no sean vendidos a terceros.\n\n" +
                                    "5. Modificaciones\n" +
                                    "WorkConnect puede actualizar estos términos en cualquier momento. El uso continuo de la app significa que aceptas las modificaciones.",
                            fontSize = 14.sp,
                            color = Color.DarkGray,
                            lineHeight = 22.sp,
                            textAlign = TextAlign.Justify
                        )
                    }

                    // 3. BOTONES INFERIORES
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = alCerrar) {
                            Text("Cerrar", color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                alAceptar()
                                alCerrar()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A365D)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Aceptar", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
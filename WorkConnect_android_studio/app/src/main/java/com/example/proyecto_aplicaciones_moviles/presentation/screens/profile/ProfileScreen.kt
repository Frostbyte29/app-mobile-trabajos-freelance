package com.example.proyecto_aplicaciones_moviles.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAddAlt1
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyecto_aplicaciones_moviles.core.utils.SessionManager
import com.example.proyecto_aplicaciones_moviles.di.AppContainer
import com.example.proyecto_aplicaciones_moviles.presentation.components.WorkConnectTextField

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = viewModel(factory = AppContainer.SharedViewModelFactory)
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.roleAddedSuccess) {
        if (state.roleAddedSuccess) { kotlinx.coroutines.delay(3000); viewModel.limpiarMensajeExito() }
    }
    LaunchedEffect(state.editSuccess) {
        if (state.editSuccess) { kotlinx.coroutines.delay(3000); viewModel.limpiarExitoEdicion() }
    }

    if (SessionManager.isGuest) { GuestProfileContent(); return }
    if (state.isLoading) {
        Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(color = Color(0xFF1A365D)) }
        return
    }
    if (state.errorMessage != null && state.userId == null) {
        ErrorProfileContent(state.errorMessage!!) { viewModel.cargarPerfilUsuario() }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))

        // BARRA SUPERIOR: título + logout
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Text("Mi Perfil", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A365D))
            IconButton(onClick = { viewModel.logout(); onLogout() }) {
                Icon(Icons.Filled.Logout, "Cerrar sesión", tint = Color(0xFF1A365D))
            }
        }

        Spacer(Modifier.height(24.dp))

        // AVATAR con inicial
        Box(
            modifier = Modifier.size(88.dp).clip(CircleShape).background(Color(0xFF1A365D)),
            contentAlignment = Alignment.Center
        ) {
            if (state.nombres.isNotBlank()) {
                Text(state.nombres.first().uppercaseChar().toString(), fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color.White)
            } else {
                Icon(Icons.Filled.Person, "Avatar", tint = Color.White, modifier = Modifier.size(48.dp))
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── SECCIÓN DATOS PERSONALES ──────────────────────────────────────
        when (state.editMode) {
            "personal" -> EditPersonalSection(state, viewModel)
            else -> DataSection(state) { viewModel.iniciarEdicionPersonal() }
        }

        if (state.editSuccess && state.editMode == "none") {
            Spacer(Modifier.height(8.dp))
            SuccessBanner("¡Datos actualizados con éxito!")
        }

        Spacer(Modifier.height(32.dp))
        HorizontalDivider(color = Color.LightGray)
        Spacer(Modifier.height(24.dp))

        // ── SECCIÓN ROLES ─────────────────────────────────────────────────
        Text("Mis Perfiles", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.fillMaxWidth())
        Text("Toca una tarjeta para cambiar el perfil activo.", fontSize = 13.sp, color = Color.Gray, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))

        if (state.roles.contains("candidato")) {
            RoleCard(
                title = "Perfil Trabajador",
                description = "Buscas proyectos y oportunidades freelance.",
                icon = Icons.Filled.WorkOutline,
                isSelected = state.activeRole == "candidato",
                onClick = { viewModel.seleccionarRol("candidato") }
            )
            Spacer(Modifier.height(12.dp))
        }

        if (state.roles.contains("reclutador")) {
            RoleCard(
                title = "Perfil Reclutador",
                description = "Publicas proyectos y contratas talento.",
                icon = Icons.Filled.PersonAddAlt1,
                isSelected = state.activeRole == "reclutador",
                onClick = { viewModel.seleccionarRol("reclutador") }
            )
            Spacer(Modifier.height(12.dp))
        }

        if (state.roleAddedSuccess) {
            SuccessBanner("¡Perfil agregado con éxito!")
            Spacer(Modifier.height(12.dp))
        }

        // Botón agregar segundo rol
        val tieneSoloUnRol = state.roles.size == 1 && !state.roleAddedSuccess
        if (tieneSoloUnRol) {
            val rolFaltante = if (state.roles.contains("candidato")) "Reclutador" else "Trabajador"
            if (state.errorMessage != null && state.editMode == "none") {
                Text(state.errorMessage!!, color = MaterialTheme.colorScheme.error, fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
            }
            if (state.isSavingRole) {
                Box(Modifier.fillMaxWidth(), Alignment.Center) { CircularProgressIndicator(color = Color(0xFF1A365D)) }
            } else {
                OutlinedButton(
                    onClick = { viewModel.agregarSegundoRol() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent)
                ) {
                    Icon(Icons.Filled.PersonAddAlt1, null, tint = Color(0xFF1A365D), modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Crear perfil $rolFaltante", color = Color(0xFF1A365D), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        // ── SECCIÓN EMPRESA (solo si tiene rol reclutador) ────────────────
        if (state.roles.contains("reclutador")) {
            Spacer(Modifier.height(24.dp))
            HorizontalDivider(color = Color.LightGray)
            Spacer(Modifier.height(24.dp))

            when (state.editMode) {
                "empresa" -> EditEmpresaSection(state, viewModel)
                else -> EmpresaDataSection(state) { viewModel.iniciarEdicionEmpresa() }
            }
        }

        Spacer(Modifier.height(80.dp))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SECCIÓN: Datos personales (modo lectura)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun DataSection(state: ProfileState, onEditClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text("${state.nombres} ${state.apellidos}".trim(), fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(state.correo, fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
        if (state.telefono.isNotBlank() && state.telefono != "+51000000000") {
            Text(state.telefono, fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(top = 2.dp))
        }
        Spacer(Modifier.height(16.dp))
        OutlinedButton(
            onClick = onEditClick,
            modifier = Modifier.height(40.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent)
        ) {
            Icon(Icons.Filled.Edit, "Editar", tint = Color(0xFF1A365D), modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text("Editar datos personales", color = Color(0xFF1A365D), fontSize = 13.sp)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SECCIÓN: Formulario editar datos personales
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun EditPersonalSection(state: ProfileState, viewModel: ProfileViewModel) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Editar datos personales", fontSize = 16.sp, fontWeight = FontWeight.Bold,
            color = Color(0xFF1A365D), modifier = Modifier.padding(bottom = 16.dp))

        Text("Nombres", fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(4.dp))
        WorkConnectTextField(value = state.editNombres, onValueChange = viewModel::onEditNombresChange, label = "Ej. Ana")

        Spacer(Modifier.height(12.dp))
        Text("Apellidos", fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(4.dp))
        WorkConnectTextField(value = state.editApellidos, onValueChange = viewModel::onEditApellidosChange, label = "Ej. Torres")

        Spacer(Modifier.height(12.dp))
        Text("Teléfono", fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(4.dp))
        WorkConnectTextField(value = state.editTelefono, onValueChange = viewModel::onEditTelefonoChange, label = "Ej. +51999888777")

        if (state.errorMessage != null) {
            Text(state.errorMessage, color = MaterialTheme.colorScheme.error, fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
        }
        Spacer(Modifier.height(16.dp))
        SaveCancelRow(isSaving = state.isSavingEdit, onSave = viewModel::guardarDatosPersonales, onCancel = viewModel::cancelarEdicion)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SECCIÓN: Datos de empresa (modo lectura) — solo para reclutadores
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun EmpresaDataSection(state: ProfileState, onEditClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Text("Datos de Empresa", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            OutlinedButton(
                onClick = onEditClick,
                modifier = Modifier.height(36.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Icon(Icons.Filled.Edit, "Editar empresa", tint = Color(0xFF1A365D), modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text("Editar", color = Color(0xFF1A365D), fontSize = 12.sp)
            }
        }

        Spacer(Modifier.height(12.dp))

        // Si no tiene datos de empresa aún, mostramos un mensaje invitando a completarlos
        if (state.empresaNombre.isBlank()) {
            Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = Color(0xFFF0F4FF)) {
                Text(
                    text = "Completa los datos de tu empresa para que los candidatos te conozcan mejor.",
                    color = Color(0xFF1A365D),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            // Mostramos los datos que ya tiene
            EmpresaDataRow(label = "Nombre", value = state.empresaNombre)
            if (state.empresaRubro.isNotBlank()) EmpresaDataRow(label = "Rubro", value = state.empresaRubro)
            if (state.empresaCorreoContacto.isNotBlank()) EmpresaDataRow(label = "Correo", value = state.empresaCorreoContacto)
            if (state.empresaTelefono.isNotBlank()) EmpresaDataRow(label = "Teléfono", value = state.empresaTelefono)
            if (state.empresaSitioWeb.isNotBlank()) EmpresaDataRow(label = "Sitio web", value = state.empresaSitioWeb)
            if (state.empresaDireccion.isNotBlank()) EmpresaDataRow(label = "Dirección", value = state.empresaDireccion)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SECCIÓN: Formulario editar datos de empresa
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun EditEmpresaSection(state: ProfileState, viewModel: ProfileViewModel) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Datos de Empresa", fontSize = 16.sp, fontWeight = FontWeight.Bold,
            color = Color(0xFF1A365D), modifier = Modifier.padding(bottom = 16.dp))

        Text("Nombre de la empresa *", fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(4.dp))
        WorkConnectTextField(value = state.editEmpresaNombre, onValueChange = viewModel::onEditEmpresaNombreChange, label = "Ej. Mi Startup SAC")

        Spacer(Modifier.height(12.dp))
        Text("Rubro", fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(4.dp))
        WorkConnectTextField(value = state.editEmpresaRubro, onValueChange = viewModel::onEditEmpresaRubroChange, label = "Ej. Tecnología")

        Spacer(Modifier.height(12.dp))
        Text("Correo de contacto", fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(4.dp))
        WorkConnectTextField(value = state.editEmpresaCorreoContacto, onValueChange = viewModel::onEditEmpresaCorreoChange, label = "Ej. contacto@empresa.com")

        Spacer(Modifier.height(12.dp))
        Text("Teléfono", fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(4.dp))
        WorkConnectTextField(value = state.editEmpresaTelefono, onValueChange = viewModel::onEditEmpresaTelefonoChange, label = "Ej. +51999111222")

        Spacer(Modifier.height(12.dp))
        Text("Sitio web", fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(4.dp))
        WorkConnectTextField(value = state.editEmpresaSitioWeb, onValueChange = viewModel::onEditEmpresaSitioWebChange, label = "Ej. https://miempresa.pe")

        Spacer(Modifier.height(12.dp))
        Text("Dirección", fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(4.dp))
        WorkConnectTextField(value = state.editEmpresaDireccion, onValueChange = viewModel::onEditEmpresaDireccionChange, label = "Ej. Av. La Mar 123, Miraflores")

        if (state.errorMessage != null) {
            Text(state.errorMessage, color = MaterialTheme.colorScheme.error, fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
        }
        Spacer(Modifier.height(16.dp))
        SaveCancelRow(isSaving = state.isSavingEdit, onSave = viewModel::guardarDatosEmpresa, onCancel = viewModel::cancelarEdicion)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// COMPONENTE: Fila de dato de empresa (label + valor)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun EmpresaDataRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text("$label: ", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray)
        Text(value, fontSize = 14.sp, color = Color.Gray)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// COMPONENTE: Botones Guardar / Cancelar reutilizables
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun SaveCancelRow(isSaving: Boolean, onSave: () -> Unit, onCancel: () -> Unit) {
    if (isSaving) {
        Box(Modifier.fillMaxWidth(), Alignment.Center) { CircularProgressIndicator(color = Color(0xFF1A365D)) }
    } else {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(8.dp)) {
                Text("Cancelar", color = Color(0xFF1A365D))
            }
            Button(onClick = onSave, modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A365D))) {
                Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Guardar", color = Color.White)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// COMPONENTE: Tarjeta de rol seleccionable
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun RoleCard(title: String, description: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    val borderColor = if (isSelected) Color(0xFF1A365D) else Color.LightGray
    val bgColor = if (isSelected) Color(0xFFF0F4FF) else Color.White

    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
            .border(if (isSelected) 2.dp else 1.dp, borderColor, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp), color = bgColor
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(Color(0xFF1A365D)), contentAlignment = Alignment.Center) {
                Icon(icon, title, tint = Color.White, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text(description, fontSize = 13.sp, color = Color.Gray, lineHeight = 18.sp)
            }
            Surface(shape = RoundedCornerShape(8.dp),
                color = if (isSelected) Color(0xFF6EE7B7).copy(alpha = 0.3f) else Color(0xFFF3F4F6)) {
                Text(if (isSelected) "Activo" else "Usar",
                    color = if (isSelected) Color(0xFF047857) else Color.Gray,
                    fontSize = 11.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// COMPONENTE: Banner de éxito
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun SuccessBanner(text: String) {
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = Color(0xFFD1FAE5)) {
        Text(text, color = Color(0xFF065F46), modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun GuestProfileContent() {
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA)), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Icon(Icons.Filled.Person, "Invitado", tint = Color.LightGray, modifier = Modifier.size(72.dp))
            Spacer(Modifier.height(16.dp))
            Text("No has iniciado sesión", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Gray, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Text("Inicia sesión para ver y gestionar tu perfil.", fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun ErrorProfileContent(message: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA)), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Text(message, fontSize = 14.sp, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
            Spacer(Modifier.height(16.dp))
            Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A365D)), shape = RoundedCornerShape(8.dp)) {
                Text("Reintentar", color = Color.White)
            }
        }
    }
}

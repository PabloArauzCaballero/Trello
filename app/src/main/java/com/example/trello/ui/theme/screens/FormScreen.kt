package com.example.trello.ui.theme.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.trello.data.enums.tarea.Prioridad
import com.example.trello.ui.theme.viewmodels.TareaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    taskId: Int? = null,
    viewModel: TareaViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var prioridadExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(taskId) {
        when {
            taskId != null -> viewModel.abrirEditorPorId(taskId)
            state.editorState == null -> viewModel.abrirEditorNuevaTarea()
        }
    }

    LaunchedEffect(state.successMessage) {
        if (state.successMessage != null) {
            navController.popBackStack()
            viewModel.limpiarMensajes()
        }
    }

    val editor = state.editorState
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = editor?.fechaVencimientoMillis)

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.actualizarEditor(fechaVencimientoMillis = datePickerState.selectedDateMillis)
                    showDatePicker = false
                }) {
                    Text(text = "Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(text = "Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = if (editor?.idTarea == null) "Nueva tarea" else "Editar tarea")
                },
                navigationIcon = {
                    TextButton(onClick = {
                        viewModel.cerrarEditor()
                        navController.popBackStack()
                    }) {
                        Text(text = "Volver")
                    }
                },
                actions = {
                    TextButton(onClick = {
                        viewModel.guardarTarea()
                    }) {
                        Text(text = "Guardar")
                    }
                }
            )
        }
    ) { padding ->
        if (editor == null) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Cargando formulario...")
            }
            return@Scaffold
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            state.errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error
                )
            }
            OutlinedTextField(
                value = editor.titulo,
                onValueChange = { viewModel.actualizarEditor(titulo = it) },
                label = { Text(text = "Titulo") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = editor.descripcion,
                onValueChange = { viewModel.actualizarEditor(descripcion = it) },
                label = { Text(text = "Descripcion") },
                modifier = Modifier.fillMaxWidth()
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "Prioridad")
                TextButton(onClick = { prioridadExpanded = true }) {
                    Text(text = editor.prioridad.name)
                }
                DropdownMenu(
                    expanded = prioridadExpanded,
                    onDismissRequest = { prioridadExpanded = false }
                ) {
                    Prioridad.entries.forEach { prioridad ->
                        DropdownMenuItem(
                            text = { Text(text = prioridad.name) },
                            onClick = {
                                prioridadExpanded = false
                                viewModel.actualizarEditor(prioridad = prioridad)
                            }
                        )
                    }
                }
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "Etiquetas")
                if (state.etiquetas.isEmpty()) {
                    Text(
                        text = "No hay etiquetas creadas",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    state.etiquetas.forEach { etiqueta ->
                        val seleccionada = editor.selectedEtiquetaIds.contains(etiqueta.idEtiqueta)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = seleccionada,
                                onCheckedChange = { checked ->
                                    viewModel.toggleEtiqueta(etiqueta.idEtiqueta, checked)
                                }
                            )
                            Text(text = etiqueta.nombre)
                        }
                    }
                }
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "Fecha de vencimiento")
                TextButton(onClick = { showDatePicker = true }) {
                    Text(text = editor.fechaVencimientoMillis?.let { Date(it).formato() } ?: "Sin fecha")
                }
                if (editor.fechaVencimientoMillis != null) {
                    TextButton(onClick = { viewModel.actualizarEditor(fechaVencimientoMillis = null) }) {
                        Text(text = "Quitar fecha")
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(onClick = {
                viewModel.guardarTarea()
            }) {
                Text(text = "Guardar tarea")
            }
        }
    }
}

private fun Date.formato(): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(this)
}

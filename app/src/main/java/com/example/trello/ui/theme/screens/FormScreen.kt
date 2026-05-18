package com.example.trello.ui.theme.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    var etiquetasExpanded by remember { mutableStateOf(false) }
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
                }) { Text(text = "Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text(text = "Cancelar") }
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
                    }) { Text(text = "Volver") }
                },
                actions = {
                    TextButton(onClick = { viewModel.guardarTarea() }) {
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
            state.errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            OutlinedTextField(
                value = editor.titulo,
                onValueChange = { viewModel.actualizarEditor(titulo = it) },
                label = { Text(text = "Título") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = editor.descripcion,
                onValueChange = { viewModel.actualizarEditor(descripcion = it) },
                label = { Text(text = "Descripción") },
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = prioridadExpanded,
                onExpandedChange = { prioridadExpanded = it }
            ) {
                OutlinedTextField(
                    value = editor.prioridad.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(text = "Prioridad") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = prioridadExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(
                    expanded = prioridadExpanded,
                    onDismissRequest = { prioridadExpanded = false }
                ) {
                    Prioridad.entries.forEach { prioridad ->
                        DropdownMenuItem(
                            text = { Text(text = prioridad.name) },
                            onClick = {
                                viewModel.actualizarEditor(prioridad = prioridad)
                                prioridadExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }

            if (state.etiquetas.isNotEmpty()) {
                val etiquetasLabel = editor.selectedEtiquetaIds
                    .mapNotNull { id -> state.etiquetas.find { it.idEtiqueta == id }?.nombre }
                    .joinToString(", ")
                    .ifBlank { "Ninguna" }

                ExposedDropdownMenuBox(
                    expanded = etiquetasExpanded,
                    onExpandedChange = { etiquetasExpanded = it }
                ) {
                    OutlinedTextField(
                        value = etiquetasLabel,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(text = "Etiquetas") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = etiquetasExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = etiquetasExpanded,
                        onDismissRequest = { etiquetasExpanded = false }
                    ) {
                        state.etiquetas.forEach { etiqueta ->
                            val seleccionada = editor.selectedEtiquetaIds.contains(etiqueta.idEtiqueta)
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Checkbox(
                                            checked = seleccionada,
                                            onCheckedChange = null
                                        )
                                        Text(text = etiqueta.nombre)
                                    }
                                },
                                onClick = { viewModel.toggleEtiqueta(etiqueta.idEtiqueta, !seleccionada) },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "Fecha de vencimiento", style = MaterialTheme.typography.labelLarge)
                Box {
                    OutlinedTextField(
                        value = editor.fechaVencimientoMillis?.let { Date(it).formato() } ?: "Sin fecha",
                        onValueChange = {},
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledContainerColor = MaterialTheme.colorScheme.surface,
                        )
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { showDatePicker = true }
                    )
                }
                if (editor.fechaVencimientoMillis != null) {
                    TextButton(onClick = { viewModel.limpiarFechaVencimiento() }) {
                        Text(text = "Quitar fecha")
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = { viewModel.guardarTarea() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1976D2)
                )
            ) {
                Text(text = "Guardar tarea", color = Color.White)
            }
        }
    }
}

private fun Date.formato(): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(this)
}
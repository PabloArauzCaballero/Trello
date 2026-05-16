package com.example.trello.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FindInPage
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.trello.data.enums.tarea.Estado
import com.example.trello.data.enums.tarea.TareaOrden
import com.example.trello.ui.theme.states.TareaItemUi
import com.example.trello.ui.theme.viewmodels.TareaViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TareaScreen(
    viewModel: TareaViewModel = hiltViewModel(LocalActivity.current as ComponentActivity),
    onNavigateToForm: (() -> Unit)? = null,
    onNavigateToEdit: ((Int) -> Unit)? = null,
    onNavigateToEtiquetas: (() -> Unit)? = null,
    onNavigateToFiltro: (() -> Unit)? = null,
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var deleteTarget by remember { mutableStateOf<TareaItemUi?>(null) }
    var ordenExpanded by remember { mutableStateOf(false) }

    val filtrosActivos = state.searchQuery.isNotBlank()
            || state.filterEstado != null
            || state.filterPrioridad != null
            || state.filterEtiquetaId != null

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            scope.launch { snackbarHostState.showSnackbar(it) }
            viewModel.limpiarMensajes()
        }
    }

    LaunchedEffect(state.successMessage) {
        state.successMessage?.let {
            scope.launch { snackbarHostState.showSnackbar(it) }
            viewModel.limpiarMensajes()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Tareas") },
                actions = {
                    Box {
                        IconButton(onClick = { ordenExpanded = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Ordenar"
                            )
                        }
                        DropdownMenu(
                            expanded = ordenExpanded,
                            onDismissRequest = { ordenExpanded = false }
                        ) {
                            val opcionesOrden = listOf(
                                TareaOrden.TITULO to "Título",
                                TareaOrden.PRIORIDAD to "Prioridad",
                                TareaOrden.FECHA_CREACION to "Fecha de creación",
                                TareaOrden.FECHA_VENCIMIENTO to "Fecha de vencimiento",
                            )
                            opcionesOrden.forEach { (orden, label) ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = label,
                                            color = if (state.orden == orden)
                                                MaterialTheme.colorScheme.primary
                                            else
                                                MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    onClick = {
                                        viewModel.actualizarOrden(orden)
                                        ordenExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    BadgedBox(
                        badge = { if (filtrosActivos) Badge() }
                    ) {
                        IconButton(onClick = { onNavigateToFiltro?.invoke() }) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filtros"
                            )
                        }
                    }
                    if (onNavigateToEtiquetas != null) {
                        IconButton(onClick = onNavigateToEtiquetas) {
                            Icon(
                                imageVector = Icons.Default.Label,
                                contentDescription = "Etiquetas"
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToForm?.invoke() }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar tarea")
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(state.tareas, key = { it.tarea.idTarea }) { item ->
                TareaCard(
                    item = item,
                    onToggle = { completada -> viewModel.cambiarEstado(item, completada) },
                    onEdit = { onNavigateToEdit?.invoke(item.tarea.idTarea) },
                    onDelete = { deleteTarget = item },
                    onDetail = { viewModel.abrirDetalle(item) }
                )
            }

            if (state.tareas.isEmpty()) {
                item {
                    Text(
                        text = if (filtrosActivos) "No hay tareas con estos filtros." else "No hay tareas.",
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }

    deleteTarget?.let { item ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.eliminarTarea(item)
                    deleteTarget = null
                }) {
                    Text(text = "Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) {
                    Text(text = "Cancelar")
                }
            },
            title = { Text(text = "Eliminar tarea") },
            text = { Text(text = "¿Está seguro que desea eliminar la tarea?") }
        )
    }

    state.detailTarea?.let { detail ->
        TareaDetalleDialog(
            item = detail,
            onDismiss = { viewModel.cerrarDetalle() }
        )
    }
}

@Composable
private fun TareaCard(
    item: TareaItemUi,
    onToggle: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDetail: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        ListItem(
            headlineContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val color = prioridadColor(item.tarea.prioridad.name, MaterialTheme.colorScheme)
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(color, CircleShape)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = item.tarea.titulo,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            },
            supportingContent = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "Prioridad: ${item.tarea.prioridad.name}")
                    item.tarea.fechaVencimiento?.let { fecha ->
                        Text(text = "Vence: ${fecha.formato()}")
                    }
                    if (item.etiquetas.isNotEmpty()) {
                        Text(text = "Etiquetas: ${item.etiquetas.joinToString()}")
                    }
                }
            },
            leadingContent = {
                Checkbox(
                    checked = item.tarea.estado == Estado.COMPLETADA,
                    onCheckedChange = onToggle
                )
            },
            trailingContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDetail) {
                        Icon(
                            imageVector = Icons.Default.FindInPage,
                            contentDescription = "Detalle",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        )
    }
}

private fun prioridadColor(prioridad: String, colors: androidx.compose.material3.ColorScheme): Color {
    return when (prioridad) {
        "ALTA" -> colors.error
        "MEDIA" -> Color(0xFFFFC107)
        else -> colors.primary
    }
}

@Composable
private fun TareaDetalleDialog(
    item: TareaItemUi,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cerrar")
            }
        },
        title = { Text(text = "Detalle de tarea") },
        text = {
            val descripcion = item.tarea.descripcion ?: "Sin descripción"
            val vencimiento = item.tarea.fechaVencimiento?.formato() ?: "Sin fecha"

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Título: ${item.tarea.titulo}")
                Text(text = "Descripción: $descripcion")
                Text(text = "Estado: ${item.tarea.estado}")
                Text(text = "Prioridad: ${item.tarea.prioridad}")
                Text(text = "Creada: ${item.tarea.fechaCreacion.formato()}")
                Text(text = "Vencimiento: $vencimiento")
                if (item.etiquetas.isNotEmpty()) {
                    Text(text = "Etiquetas: ${item.etiquetas.joinToString()}")
                }
            }
        }
    )
}

private fun Date.formato(): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(this)
}
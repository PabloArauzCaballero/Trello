package com.example.trello.ui.theme.screens

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.trello.data.enums.tarea.Estado
import com.example.trello.data.enums.tarea.Prioridad
import com.example.trello.ui.theme.viewmodels.TareaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltroScreen(
    navController: NavHostController,
    viewModel: TareaViewModel = hiltViewModel(LocalActivity.current as ComponentActivity),
) {
    val state by viewModel.uiState.collectAsState()
    var etiquetaExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Buscar y filtrar") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            viewModel.actualizarBusqueda("")
                            viewModel.actualizarFiltroEstado(null)
                            viewModel.actualizarFiltroPrioridad(null)
                            viewModel.actualizarFiltroEtiqueta(null)
                        }
                    ) {
                        Text(text = "Limpiar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.actualizarBusqueda(it) },
                label = { Text(text = "Buscar por título") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Estado", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = state.filterEstado == null,
                        onClick = { viewModel.actualizarFiltroEstado(null) },
                        label = { Text(text = "Todos") }
                    )
                    FilterChip(
                        selected = state.filterEstado == Estado.PENDIENTE,
                        onClick = { viewModel.actualizarFiltroEstado(Estado.PENDIENTE) },
                        label = { Text(text = "Pendiente") }
                    )
                    FilterChip(
                        selected = state.filterEstado == Estado.COMPLETADA,
                        onClick = { viewModel.actualizarFiltroEstado(Estado.COMPLETADA) },
                        label = { Text(text = "Completada") }
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Prioridad", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = state.filterPrioridad == null,
                        onClick = { viewModel.actualizarFiltroPrioridad(null) },
                        label = { Text(text = "Todas") }
                    )
                    FilterChip(
                        selected = state.filterPrioridad == Prioridad.BAJA,
                        onClick = { viewModel.actualizarFiltroPrioridad(Prioridad.BAJA) },
                        label = { Text(text = "Baja") }
                    )
                    FilterChip(
                        selected = state.filterPrioridad == Prioridad.MEDIA,
                        onClick = { viewModel.actualizarFiltroPrioridad(Prioridad.MEDIA) },
                        label = { Text(text = "Media") }
                    )
                    FilterChip(
                        selected = state.filterPrioridad == Prioridad.ALTA,
                        onClick = { viewModel.actualizarFiltroPrioridad(Prioridad.ALTA) },
                        label = { Text(text = "Alta") }
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Etiqueta", style = MaterialTheme.typography.labelLarge)
                val etiquetaActual = state.etiquetas.firstOrNull { it.idEtiqueta == state.filterEtiquetaId }
                TextButton(onClick = { etiquetaExpanded = true }) {
                    Text(text = etiquetaActual?.nombre ?: "Todas")
                }
                DropdownMenu(
                    expanded = etiquetaExpanded,
                    onDismissRequest = { etiquetaExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(text = "Todas") },
                        onClick = {
                            etiquetaExpanded = false
                            viewModel.actualizarFiltroEtiqueta(null)
                        }
                    )
                    state.etiquetas.forEach { etiqueta ->
                        DropdownMenuItem(
                            text = { Text(text = etiqueta.nombre) },
                            onClick = {
                                etiquetaExpanded = false
                                viewModel.actualizarFiltroEtiqueta(etiqueta.idEtiqueta)
                            }
                        )
                    }
                }
            }
        }
    }
}
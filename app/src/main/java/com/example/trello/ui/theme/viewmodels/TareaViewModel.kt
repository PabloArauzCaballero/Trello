package com.example.trello.ui.theme.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trello.data.entities.Tarea
import com.example.trello.data.enums.tarea.Estado
import com.example.trello.data.enums.tarea.Prioridad
import com.example.trello.data.enums.tarea.TareaOrden
import com.example.trello.data.repository.EtiquetaRepository
import com.example.trello.data.repository.TareaRepository
import com.example.trello.ui.theme.states.TareaEditorState
import com.example.trello.ui.theme.states.TareaItemUi
import com.example.trello.ui.theme.states.TareaUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class TareaViewModel @Inject constructor(
    private val tareaRepository: TareaRepository,
    private val etiquetaRepository: EtiquetaRepository
) : ViewModel() {

    private data class TareaFiltros(
        val query: String = "",
        val estado: Estado? = null,
        val prioridad: Prioridad? = null,
        val etiquetaId: Int? = null,
        val orden: TareaOrden = TareaOrden.FECHA_CREACION,
    )

    private val filtrosState = MutableStateFlow(TareaFiltros())
    private val _uiState = MutableStateFlow(TareaUiState(isLoading = true))
    val uiState: StateFlow<TareaUiState> = _uiState.asStateFlow()

    init {
        observarTareas()
        observarEtiquetas()
    }

    private fun observarTareas() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            filtrosState
                .flatMapLatest { filtro ->
                    tareaRepository.getTareasFiltradasFlow(
                        query = filtro.query.ifBlank { null },
                        estado = filtro.estado,
                        prioridad = filtro.prioridad,
                        etiquetaId = filtro.etiquetaId,
                        orden = filtro.orden,
                    ).map { relaciones ->
                        relaciones.map { relacion ->
                            TareaItemUi(
                                tarea = relacion.tarea,
                                etiquetas = relacion.etiquetas.map { it.nombre },
                                etiquetaIds = relacion.etiquetas.map { it.idEtiqueta }
                            )
                        }
                    }
                }
                .catch { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
                .collect { tareas ->
                    _uiState.update { it.copy(isLoading = false, tareas = tareas) }
                }
        }
    }

    private fun observarEtiquetas() {
        viewModelScope.launch {
            etiquetaRepository.getEtiquetasFlow()
                .catch { error ->
                    _uiState.update { it.copy(errorMessage = error.message) }
                }
                .collect { etiquetas ->
                    _uiState.update { it.copy(etiquetas = etiquetas) }
                }
        }
    }

    fun abrirEditorNuevaTarea() {
        _uiState.update { it.copy(editorState = TareaEditorState()) }
    }

    fun abrirEditorPorId(idTarea: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val relacion = tareaRepository.getTareaConEtiquetaById(idTarea)
            if (relacion == null) {
                _uiState.update { it.copy(errorMessage = "No se encontro la tarea.") }
                return@launch
            }
            val tarea = relacion.tarea
            _uiState.update {
                it.copy(
                    editorState = TareaEditorState(
                        idTarea = tarea.idTarea,
                        titulo = tarea.titulo,
                        descripcion = tarea.descripcion.orEmpty(),
                        prioridad = tarea.prioridad,
                        fechaVencimientoMillis = tarea.fechaVencimiento?.time,
                        estado = tarea.estado,
                        fechaCreacionMillis = tarea.fechaCreacion.time,
                        selectedEtiquetaIds = relacion.etiquetas.map { it.idEtiqueta }
                    )
                )
            }
        }
    }

    fun cerrarEditor() {
        _uiState.update { it.copy(editorState = null) }
    }

    fun actualizarEditor(
        titulo: String? = null,
        descripcion: String? = null,
        prioridad: Prioridad? = null,
        fechaVencimientoMillis: Long? = null,
        selectedEtiquetaIds: List<Int>? = null,
    ) {
        _uiState.update { state ->
            val editor = state.editorState ?: return@update state
            state.copy(
                editorState = editor.copy(
                    titulo = titulo ?: editor.titulo,
                    descripcion = descripcion ?: editor.descripcion,
                    prioridad = prioridad ?: editor.prioridad,
                    fechaVencimientoMillis = fechaVencimientoMillis
                        ?: editor.fechaVencimientoMillis,
                    selectedEtiquetaIds = selectedEtiquetaIds ?: editor.selectedEtiquetaIds
                )
            )
        }
    }

    fun toggleEtiqueta(idEtiqueta: Int, seleccionada: Boolean) {
        val editor = _uiState.value.editorState ?: return
        val actuales = editor.selectedEtiquetaIds.toMutableSet()
        if (seleccionada) {
            actuales.add(idEtiqueta)
        } else {
            actuales.remove(idEtiqueta)
        }
        actualizarEditor(selectedEtiquetaIds = actuales.toList())
    }

    fun guardarTarea() {
        val editor = _uiState.value.editorState ?: return
        if (editor.titulo.isBlank()) {
            _uiState.update { it.copy(errorMessage = "El titulo es obligatorio.") }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val tarea = Tarea(
                idTarea = editor.idTarea ?: 0,
                titulo = editor.titulo.trim(),
                descripcion = editor.descripcion.ifBlank { null },
                fechaVencimiento = editor.fechaVencimientoMillis?.let { Date(it) },
                prioridad = editor.prioridad,
                estado = editor.estado,
                fechaCreacion = editor.fechaCreacionMillis?.let { Date(it) } ?: Date(),
            )

            if (editor.idTarea == null) {
                if (editor.selectedEtiquetaIds.isEmpty()) {
                    tareaRepository.insertTarea(tarea)
                } else {
                    tareaRepository.insertTareaConEtiquetas(tarea, editor.selectedEtiquetaIds)
                }
                _uiState.update { it.copy(successMessage = "Tarea creada") }
            } else {
                tareaRepository.updateTareaConEtiquetas(tarea, editor.selectedEtiquetaIds)
                _uiState.update { it.copy(successMessage = "Tarea actualizada") }
            }

            cerrarEditor()
        }
    }

    fun limpiarMensajes() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    fun eliminarTarea(item: TareaItemUi) {
        viewModelScope.launch(Dispatchers.IO) {
            tareaRepository.deleteTarea(item.tarea)
        }
    }

    fun cambiarEstado(item: TareaItemUi, completada: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val estado = if (completada) Estado.COMPLETADA else Estado.PENDIENTE
            val actualizada = item.tarea.copy(estado = estado)
            tareaRepository.updateTarea(actualizada)
        }
    }

    fun abrirDetalle(item: TareaItemUi) {
        _uiState.update { it.copy(detailTarea = item) }
    }

    fun cerrarDetalle() {
        _uiState.update { it.copy(detailTarea = null) }
    }

    fun actualizarBusqueda(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        filtrosState.update { it.copy(query = query) }
    }

    fun actualizarFiltroEstado(estado: Estado?) {
        _uiState.update { it.copy(filterEstado = estado) }
        filtrosState.update { it.copy(estado = estado) }
    }

    fun actualizarFiltroPrioridad(prioridad: Prioridad?) {
        _uiState.update { it.copy(filterPrioridad = prioridad) }
        filtrosState.update { it.copy(prioridad = prioridad) }
    }

    fun actualizarFiltroEtiqueta(idEtiqueta: Int?) {
        _uiState.update { it.copy(filterEtiquetaId = idEtiqueta) }
        filtrosState.update { it.copy(etiquetaId = idEtiqueta) }
    }

    fun actualizarOrden(orden: TareaOrden) {
        _uiState.update { it.copy(orden = orden) }
        filtrosState.update { it.copy(orden = orden) }
    }

}

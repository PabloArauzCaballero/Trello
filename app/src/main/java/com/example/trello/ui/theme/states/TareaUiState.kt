package com.example.trello.ui.theme.states

import com.example.trello.data.entities.Etiqueta
import com.example.trello.data.entities.Tarea
import com.example.trello.data.enums.tarea.Estado
import com.example.trello.data.enums.tarea.Prioridad
import com.example.trello.data.enums.tarea.TareaOrden

// UI state for task screen.
data class TareaUiState(
    val tareas: List<TareaItemUi> = emptyList(),
    val etiquetas: List<Etiqueta> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val editorState: TareaEditorState? = null,
    val detailTarea: TareaItemUi? = null,
    val searchQuery: String = "",
    val filterEstado: Estado? = null,
    val filterPrioridad: Prioridad? = null,
    val filterEtiquetaId: Int? = null,
    val orden: TareaOrden = TareaOrden.FECHA_CREACION,
)

data class TareaItemUi(
    val tarea: Tarea,
    val etiquetas: List<String> = emptyList(),
    val etiquetaIds: List<Int> = emptyList(),
)

data class TareaEditorState(
    val idTarea: Int? = null,
    val titulo: String = "",
    val descripcion: String = "",
    val prioridad: Prioridad = Prioridad.MEDIA,
    val fechaVencimientoMillis: Long? = null,
    val estado: Estado = Estado.PENDIENTE,
    val fechaCreacionMillis: Long? = null,
    val selectedEtiquetaIds: List<Int> = emptyList(),
)
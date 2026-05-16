package com.example.trello.ui.theme.states

import com.example.trello.data.entities.Etiqueta

// UI state for etiquetas screen.
data class EtiquetaUiState(
    val etiquetas: List<Etiqueta> = emptyList(),
    val nombre: String = "",
    val errorMessage: String? = null,
    val successMessage: String? = null,
)


package com.example.trello.ui.theme.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trello.data.entities.Etiqueta
import com.example.trello.data.repository.EtiquetaRepository
import com.example.trello.ui.theme.states.EtiquetaUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EtiquetaViewModel @Inject constructor(
    private val etiquetaRepository: EtiquetaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EtiquetaUiState())
    val uiState: StateFlow<EtiquetaUiState> = _uiState.asStateFlow()

    init {
        observarEtiquetas()
    }

    private fun observarEtiquetas() {
        viewModelScope.launch {
            etiquetaRepository.getEtiquetasFlow()
                .catch { error ->
                    _uiState.update { it.copy(errorMessage = error.message) }
                }
                .collect { etiquetas ->
                    _uiState.update { it.copy(etiquetas = etiquetas, errorMessage = null) }
                }
        }
    }

    fun actualizarNombre(nombre: String) {
        _uiState.update { it.copy(nombre = nombre) }
    }

    fun guardarEtiqueta() {
        val nombre = _uiState.value.nombre.trim()
        if (nombre.isBlank()) {
            _uiState.update { it.copy(errorMessage = "El nombre es obligatorio.") }
            return
        }
        val existe = _uiState.value.etiquetas.any { it.nombre.equals(nombre, ignoreCase = true) }
        if (existe) {
            _uiState.update { it.copy(errorMessage = "La etiqueta ya existe.") }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            etiquetaRepository.insertEtiqueta(Etiqueta(nombre = nombre))
            _uiState.update { it.copy(nombre = "", successMessage = "Etiqueta creada") }
        }
    }

    fun eliminarEtiqueta(etiqueta: Etiqueta) {
        viewModelScope.launch(Dispatchers.IO) {
            etiquetaRepository.deleteEtiqueta(etiqueta)
            _uiState.update { it.copy(successMessage = "Etiqueta eliminada") }
        }
    }

    fun limpiarMensajes() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}


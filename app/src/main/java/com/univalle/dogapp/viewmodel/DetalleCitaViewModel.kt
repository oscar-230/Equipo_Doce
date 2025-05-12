package com.univalle.dogapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univalle.dogapp.model.Cita
import com.univalle.dogapp.repository.CitaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetalleCitaViewModel(private val repository: CitaRepository) : ViewModel() {

    private val _cita = MutableStateFlow<Cita?>(null)
    val cita: StateFlow<Cita?> get() = _cita

    fun cargarCita(id: Int) {
        viewModelScope.launch {
            _cita.value = repository.obtenerPorId(id)
        }
    }

    fun eliminarCita(id: Int, onDeleted: () -> Unit) {
        viewModelScope.launch {
            repository.eliminarPorId(id)
            onDeleted()
        }
    }
}

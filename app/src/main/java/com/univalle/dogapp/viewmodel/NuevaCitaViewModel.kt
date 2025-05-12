package com.univalle.dogapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univalle.dogapp.service.DogApiService
import com.univalle.dogapp.model.Cita
import com.univalle.dogapp.repository.CitaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NuevaCitaViewModel(
    private val repository: CitaRepository,
    private val apiService: DogApiService
) : ViewModel() {

    private val _razas = MutableStateFlow<List<String>>(emptyList())
    val razas: StateFlow<List<String>> get() = _razas

    private val _imagenUrl = MutableStateFlow("")
    val imagenUrl: StateFlow<String> get() = _imagenUrl

    fun cargarRazas() {
        viewModelScope.launch {
            try {
                val response = apiService.obtenerListaRazas()
                _razas.value = response.message.keys.map { it.replace("-", " ") }.sorted()
            } catch (_: Exception) {}
        }
    }

    fun obtenerImagenRaza(raza: String) {
        viewModelScope.launch {
            try {
                val response = apiService.obtenerImagenRaza(raza.lowercase())
                _imagenUrl.value = response.message
            } catch (_: Exception) {
                _imagenUrl.value = ""
            }
        }
    }

    fun guardarCita(cita: Cita, onSaved: () -> Unit) {
        viewModelScope.launch {
            repository.insertar(cita)
            onSaved()
        }
    }
}

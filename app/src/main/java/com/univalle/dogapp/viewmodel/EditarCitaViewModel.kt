package com.univalle.dogapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univalle.dogapp.model.Cita
import com.univalle.dogapp.repository.CitaRepository
import com.univalle.dogapp.service.DogApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditarCitaViewModel(
    private val repository: CitaRepository,
    private val apiService: DogApiService
) : ViewModel() {

    private val _razas = MutableStateFlow<List<String>>(emptyList())
    val razas: StateFlow<List<String>> = _razas

    private val _citaActual = MutableStateFlow<Cita?>(null)
    val citaActual: StateFlow<Cita?> = _citaActual

    private val _editExitosa = MutableStateFlow(false)
    val editExitosa: StateFlow<Boolean> = _editExitosa

    private val _imagenUrl = MutableStateFlow("")
    val imagenUrl: StateFlow<String> = _imagenUrl

    fun cargarRazas() {
        viewModelScope.launch {
            try {
                val response = apiService.obtenerListaRazas()
                _razas.value = response.message.keys.map { it.replace("-", " ") }.sorted()
            } catch (e: Exception) {
                // Manejar error si es necesario
            }
        }
    }

    fun cargarCita(id: Int) {
        viewModelScope.launch {
            try {
                val cita = repository.obtenerPorId(id)
                _citaActual.value = cita
            } catch (e: Exception) {
                // Manejar error si es necesario
            }
        }
    }

    fun editarCita(cita: Cita) {
        viewModelScope.launch {
            try {
                // Obtener una nueva imagen para la raza seleccionada
                val response = apiService.obtenerImagenRaza(cita.raza.lowercase())
                val nuevaUrlImagen = response.message

                // Actualizar la cita con la nueva imagen
                val citaActualizada = cita.copy(urlImagen = nuevaUrlImagen)
                repository.actualizar(citaActualizada)
                _editExitosa.value = true
            } catch (e: Exception) {
                // Si falla la obtención de la imagen, actualizar la cita con la imagen actual
                repository.actualizar(cita)
                _editExitosa.value = true
            }
        }
    }
} 
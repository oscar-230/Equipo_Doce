package com.univalle.dogapp.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.univalle.dogapp.data.AppDatabase
import com.univalle.dogapp.databinding.ActivityEditarCitaBinding
import com.univalle.dogapp.model.Cita
import com.univalle.dogapp.repository.CitaRepository
import com.univalle.dogapp.service.ApiUtils
import com.univalle.dogapp.viewmodel.EditarCitaViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class EditarCitaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditarCitaBinding
    private var citaId: Int = -1
    private var urlImagen: String = ""

    private val viewModel: EditarCitaViewModel by viewModels {
        object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val repository = CitaRepository(AppDatabase.getDatabase(applicationContext))
                @Suppress("UNCHECKED_CAST")
                return EditarCitaViewModel(repository, ApiUtils.apiService) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarCitaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        citaId = intent.getIntExtra("CITA_ID", -1)
        if (citaId == -1) {
            finish()
            return
        }

        setupUI()
        setupListeners()
        observeViewModel()
        
        viewModel.cargarRazas()
        viewModel.cargarCita(citaId)
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validarFormulario()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        binding.etNombreMascota.addTextChangedListener(textWatcher)
        binding.actvRaza.addTextChangedListener(textWatcher)
        binding.etPropietario.addTextChangedListener(textWatcher)
        binding.etTelefono.addTextChangedListener(textWatcher)
    }

    private fun setupListeners() {
        binding.btnEditarCita.setOnClickListener {
            val cita = Cita(
                id = citaId,
                nombreMascota = binding.etNombreMascota.text.toString(),
                raza = binding.actvRaza.text.toString(),
                propietario = binding.etPropietario.text.toString(),
                telefono = binding.etTelefono.text.toString(),
                sintomas = viewModel.citaActual.value?.sintomas ?: "",
                urlImagen = urlImagen
            )
            
            viewModel.editarCita(cita)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.razas.collectLatest { razas ->
                val adapter = ArrayAdapter(
                    this@EditarCitaActivity,
                    android.R.layout.simple_dropdown_item_1line,
                    razas
                )
                binding.actvRaza.setAdapter(adapter)
            }
        }

        lifecycleScope.launch {
            viewModel.citaActual.collectLatest { cita ->
                cita?.let {
                    binding.etNombreMascota.setText(it.nombreMascota)
                    binding.actvRaza.setText(it.raza)
                    binding.etPropietario.setText(it.propietario)
                    binding.etTelefono.setText(it.telefono)
                    urlImagen = it.urlImagen
                }
            }
        }

        lifecycleScope.launch {
            viewModel.editExitosa.collectLatest { exitosa ->
                if (exitosa) {
                    Toast.makeText(this@EditarCitaActivity, "Cita actualizada exitosamente", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@EditarCitaActivity, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.imagenUrl.collectLatest {
                urlImagen = it
            }
        }
    }

    private fun validarFormulario() {
        val nombreMascota = binding.etNombreMascota.text.toString()
        val raza = binding.actvRaza.text.toString()
        val propietario = binding.etPropietario.text.toString()
        val telefono = binding.etTelefono.text.toString()

        val camposValidos = nombreMascota.isNotBlank() &&
                raza.isNotBlank() &&
                propietario.isNotBlank() &&
                telefono.length == 10

        binding.btnEditarCita.isEnabled = camposValidos
    }
} 
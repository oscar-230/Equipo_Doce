package com.univalle.dogapp.view.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.univalle.dogapp.databinding.ActivityNuevaCitaBinding
import com.univalle.dogapp.model.Cita
import com.univalle.dogapp.repository.CitaRepository
import com.univalle.dogapp.service.ApiUtils
import com.univalle.dogapp.viewmodel.NuevaCitaViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.univalle.dogapp.data.AppDatabase

class NuevaCitaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNuevaCitaBinding
    private var urlImagen = ""

    private val viewModel: NuevaCitaViewModel by viewModels {
        object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val repository = CitaRepository(AppDatabase.getDatabase(applicationContext))
                @Suppress("UNCHECKED_CAST")
                return NuevaCitaViewModel(repository, ApiUtils.apiService) as T
            }
        }
    }

    private val sintomasList = listOf(
        "Síntomas", "Solo duerme", "No come", "Fractura extremidad",
        "Tiene pulgas", "Tiene garrapatas", "Bota demasiado pelo"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNuevaCitaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observarViewModel()
        viewModel.cargarRazas()
        cargarSintomas()
    }

    private fun setupListeners() {
        binding.ivBack.setOnClickListener { finish() }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validarFormulario()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.etNombreMascota.addTextChangedListener(textWatcher)
        binding.autoCompleteRaza.addTextChangedListener(textWatcher)
        binding.etPropietario.addTextChangedListener(textWatcher)
        binding.etTelefono.addTextChangedListener(textWatcher)
        binding.spinnerSintomas.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, _, _ -> validarFormulario() }

        binding.btnGuardar.setOnClickListener {
            if (binding.spinnerSintomas.text.toString() == "Síntomas") {
                Toast.makeText(this, "Selecciona un síntoma", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val cita = Cita(
                nombreMascota = binding.etNombreMascota.text.toString(),
                raza = binding.autoCompleteRaza.text.toString(),
                propietario = binding.etPropietario.text.toString(),
                telefono = binding.etTelefono.text.toString(),
                sintomas = binding.spinnerSintomas.text.toString(),
                urlImagen = urlImagen
            )

            viewModel.guardarCita(cita) {
                Toast.makeText(this, "Cita guardada", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun cargarSintomas() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, sintomasList)
        binding.spinnerSintomas.setAdapter(adapter)

        binding.spinnerSintomas.inputType = android.text.InputType.TYPE_NULL
        binding.spinnerSintomas.keyListener = null
        binding.spinnerSintomas.setOnClickListener {
            binding.spinnerSintomas.showDropDown()
        }
    }

    private fun observarViewModel() {
        lifecycleScope.launch {
            viewModel.razas.collectLatest { razas ->
                val adapter = ArrayAdapter(this@NuevaCitaActivity, android.R.layout.simple_dropdown_item_1line, razas)
                binding.autoCompleteRaza.setAdapter(adapter)

                binding.autoCompleteRaza.setOnItemClickListener { parent, _, position, _ ->
                    val razaSeleccionada = parent.getItemAtPosition(position).toString()
                    viewModel.obtenerImagenRaza(razaSeleccionada)
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
        val camposLlenos = binding.etNombreMascota.text.toString().isNotBlank()
                && binding.autoCompleteRaza.text.toString().isNotBlank()
                && binding.etPropietario.text.toString().isNotBlank()
                && binding.etTelefono.text.toString().length == 10
                && binding.spinnerSintomas.text.toString().isNotBlank()

        binding.btnGuardar.isEnabled = camposLlenos
        binding.btnGuardar.alpha = if (camposLlenos) 1f else 0.5f

        if (camposLlenos) {
            binding.btnGuardar.setTextColor(resources.getColor(android.R.color.white, null))
            binding.btnGuardar.setTypeface(null, android.graphics.Typeface.BOLD)
        }
    }
}

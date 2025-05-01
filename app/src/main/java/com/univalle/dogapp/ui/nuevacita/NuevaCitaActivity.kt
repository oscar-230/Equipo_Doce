package com.univalle.dogapp.ui.nuevacita

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.univalle.dogapp.databinding.ActivityNuevaCitaBinding
import com.univalle.dogapp.data.local.AppDatabase
import com.univalle.dogapp.data.local.CitaEntity
import com.univalle.dogapp.data.remote.DogApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.text.Editable
import android.text.TextWatcher
import android.widget.AdapterView

class NuevaCitaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNuevaCitaBinding
    private lateinit var apiService: DogApiService
    private var razaSeleccionada = ""
    private var urlImagen = ""
    private val sintomasList = listOf(
        "Síntomas", "Solo duerme", "No come", "Fractura extremidad",
        "Tiene pulgas", "Tiene garrapatas", "Bota demasiado pelo"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNuevaCitaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarRetrofit()
        cargarRazasDesdeAPI()
        cargarSintomas()
        configurarListeners()
    }

    private fun configurarRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://dog.ceo/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(DogApiService::class.java)
    }

    private fun configurarListeners() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        // Validación en tiempo real para todos los campos de texto
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
            } else {
                guardarCitaEnRoom()
            }
        }
    }

    private fun cargarRazasDesdeAPI() {
        lifecycleScope.launch {
            try {
                val response = apiService.obtenerListaRazas()
                val razas = response.message.keys.map { it.replace("-", " ") }.sorted()
                val adapter = ArrayAdapter(this@NuevaCitaActivity, android.R.layout.simple_dropdown_item_1line, razas)
                binding.autoCompleteRaza.setAdapter(adapter)

                binding.autoCompleteRaza.setOnItemClickListener { parent, _, position, _ ->
                    razaSeleccionada = parent.getItemAtPosition(position).toString()
                    obtenerImagenDeRaza(razaSeleccionada)
                }

            } catch (e: Exception) {
                Toast.makeText(this@NuevaCitaActivity, "Error al cargar razas", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun obtenerImagenDeRaza(raza: String) {
        lifecycleScope.launch {
            try {
                val response = apiService.obtenerImagenRaza(raza.lowercase())
                urlImagen = response.message
            } catch (e: Exception) {
                urlImagen = ""
            }
        }
    }

    private fun cargarSintomas() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, sintomasList)
        binding.spinnerSintomas.setAdapter(adapter)
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


    private fun guardarCitaEnRoom() {
        val db = AppDatabase.getDatabase(this)
        val dao = db.citaDao()

        val nuevaCita = CitaEntity(
            nombreMascota = binding.etNombreMascota.text.toString(),
            raza = razaSeleccionada,
            propietario = binding.etPropietario.text.toString(),
            telefono = binding.etTelefono.text.toString(),
            sintomas = binding.spinnerSintomas.text.toString(),
            urlImagen = urlImagen
        )

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                dao.insertarCita(nuevaCita)
            }
            Toast.makeText(this@NuevaCitaActivity, "Cita guardada", Toast.LENGTH_SHORT).show()
            finish() // vuelve al Home
        }
    }
}

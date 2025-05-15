package com.univalle.dogapp.view.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.squareup.picasso.Picasso
import com.univalle.dogapp.data.local.AppDatabase
import com.univalle.dogapp.databinding.ActivityDetalleCitaBinding
import com.univalle.dogapp.repository.CitaRepository
import com.univalle.dogapp.viewmodel.DetalleCitaViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DetalleCitaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleCitaBinding
    private var citaId: Int = -1

    private val viewModel: DetalleCitaViewModel by viewModels {
        object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val repository = CitaRepository(AppDatabase.getDatabase(applicationContext))
                @Suppress("UNCHECKED_CAST")
                return DetalleCitaViewModel(repository) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleCitaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        citaId = intent.getIntExtra("CITA_ID", -1)

        setupListeners()
        observarCita()

        if (citaId != -1) {
            viewModel.cargarCita(citaId)
        } else {
            Toast.makeText(this, "ID de cita no válido", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupListeners() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.fabEliminar.setOnClickListener {
            if (citaId != -1) {
                viewModel.eliminarCita(citaId) {
                    Toast.makeText(this, "Cita eliminada", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        binding.fabEditar.setOnClickListener {
            if (citaId != -1) {
                val intent = Intent(this, EditarCitaActivity::class.java)
                intent.putExtra("CITA_ID", citaId)
                startActivity(intent)
            }
        }
    }

    private fun observarCita() {
        lifecycleScope.launch {
            viewModel.cita.collectLatest { cita ->
                cita?.let {
                    binding.tvNombreMascota.text = it.nombreMascota
                    binding.tvTurno.text = "#${it.id}"
                    binding.tvRaza.text = it.raza
                    binding.tvSintoma.text = it.sintomas
                    binding.tvPropietario.text = "Propietario: ${it.propietario}"
                    binding.tvTelefono.text = "Teléfono: ${it.telefono}"

                    if (it.urlImagen.isNotEmpty()) {
                        Picasso.get().load(it.urlImagen).into(binding.ivMascota)
                    }
                }
            }
        }
    }
}

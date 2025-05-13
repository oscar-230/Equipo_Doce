package com.univalle.dogapp.ui.detallecita

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.squareup.picasso.Picasso
import com.univalle.dogapp.R
import com.univalle.dogapp.data.local.AppDatabase
import com.univalle.dogapp.data.local.CitaEntity
import com.univalle.dogapp.databinding.ActivityDetalleCitaBinding
/*import com.univalle.dogapp.ui.editarcita.EditarCitaActivity*/
import com.univalle.dogapp.ui.home.HomeActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetalleCitaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetalleCitaBinding
    private var citaId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleCitaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        citaId = intent.getIntExtra("cita_id", 0)
        if (citaId == 0) {
            finish()
            return
        }

        setupUI()
        cargarDetalleCita()
    }

    private fun setupUI() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.fabEliminar.setOnClickListener {
            eliminarCita()
        }

        binding.fabEditar.setOnClickListener {
            /*val intent = Intent(this, EditarCitaActivity::class.java).apply {
                putExtra("cita_id", citaId)
            }
            startActivity(intent)*/
        }
    }

    private fun cargarDetalleCita() {
        val dao = AppDatabase.getDatabase(this).citaDao()

        lifecycleScope.launch {
            val cita = withContext(Dispatchers.IO) {
                dao.obtenerCitaPorId(citaId)
            }

            cita?.let { mostrarDetalleCita(it) }
        }
    }

    private fun mostrarDetalleCita(cita: CitaEntity) {
        binding.apply {
            tvNombreMascota.text = cita.nombreMascota
            tvTurno.text = "#${cita.id}"
            tvRaza.text = cita.raza
            tvSintomas.text = cita.sintomas
            tvPropietario.text = cita.propietario
            tvTelefono.text = cita.telefono

            if (cita.urlImagen.isNotEmpty()) {
                Picasso.get()
                    .load(cita.urlImagen)
                    .placeholder(R.drawable.ic_dog_placeholder)
                    .into(ivMascota)
            }
        }
    }

    private fun eliminarCita() {
        val dao = AppDatabase.getDatabase(this).citaDao()

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                dao.eliminarCita(citaId)
            }

            val intent = Intent(this@DetalleCitaActivity, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}
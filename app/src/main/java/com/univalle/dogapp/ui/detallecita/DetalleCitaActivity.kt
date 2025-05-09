package com.univalle.dogapp.ui.detallecita

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.squareup.picasso.Picasso
import com.univalle.dogapp.data.local.AppDatabase
import com.univalle.dogapp.databinding.ActivityDetalleCitaBinding
import com.univalle.dogapp.ui.home.HomeActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetalleCitaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleCitaBinding
    private var citaId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleCitaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        citaId = intent.getIntExtra("CITA_ID", -1)
        if (citaId == -1) {
            Toast.makeText(this, "Cita no encontrada", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        cargarDetalle()

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.fabEliminar.setOnClickListener {
            eliminarCita()
        }

        binding.fabEditar.setOnClickListener {
            Toast.makeText(this, "Funcionalidad de editar aún no implementada", Toast.LENGTH_SHORT).show()
            // startActivity(Intent(this, EditarCitaActivity::class.java))
        }
    }

    private fun cargarDetalle() {
        val db = AppDatabase.getDatabase(this)
        lifecycleScope.launch {
            val cita = withContext(Dispatchers.IO) {
                db.citaDao().obtenerCitaPorId(citaId)
            }

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

    private fun eliminarCita() {
        val db = AppDatabase.getDatabase(this)
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                db.citaDao().eliminarCitaPorId(citaId)
            }
            Toast.makeText(this@DetalleCitaActivity, "Cita eliminada", Toast.LENGTH_SHORT).show()
            finish() // esto basta, porque HomeActivity se refresca en onResume()
        }
    }
}

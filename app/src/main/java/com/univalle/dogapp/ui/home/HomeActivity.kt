package com.univalle.dogapp.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.univalle.dogapp.data.local.AppDatabase
import com.univalle.dogapp.data.local.CitaEntity
import com.univalle.dogapp.databinding.ActivityHomeBinding
import com.univalle.dogapp.ui.nuevacita.NuevaCitaActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var adapter: CitaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = CitaAdapter(emptyList()) { cita ->
            // Aquí irá luego navegación al detalle (HU 4.0)
        }

        binding.rvAppointments.layoutManager = LinearLayoutManager(this)
        binding.rvAppointments.adapter = adapter

        binding.fabAddAppointment.setOnClickListener {
            startActivity(Intent(this, NuevaCitaActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        cargarCitasDesdeRoom()
    }

    private fun cargarCitasDesdeRoom() {
        val dao = AppDatabase.getDatabase(this).citaDao()

        lifecycleScope.launch {
            val citas: List<CitaEntity> = withContext(Dispatchers.IO) {
                dao.obtenerCitas()
            }
            adapter.actualizarCitas(citas)
        }
    }
}

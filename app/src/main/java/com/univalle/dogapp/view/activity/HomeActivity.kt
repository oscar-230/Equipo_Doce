package com.univalle.dogapp.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.univalle.dogapp.data.AppDatabase
import com.univalle.dogapp.databinding.ActivityHomeBinding
import com.univalle.dogapp.repository.CitaRepository
import com.univalle.dogapp.view.adapter.CitaAdapter
import com.univalle.dogapp.viewmodel.HomeViewModel
import kotlinx.coroutines.flow.collectLatest

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var adapter: CitaAdapter
    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = CitaRepository(AppDatabase.getDatabase(this))
        viewModel = HomeViewModel(repository)

        adapter = CitaAdapter(emptyList()) { cita ->
            val intent = Intent(this, DetalleCitaActivity::class.java)
            intent.putExtra("CITA_ID", cita.id)
            startActivity(intent)
        }

        binding.rvAppointments.layoutManager = LinearLayoutManager(this)
        binding.rvAppointments.adapter = adapter

        binding.fabAddAppointment.setOnClickListener {
            val intent = Intent(this, NuevaCitaActivity::class.java)
            startActivity(intent)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.citas.collectLatest {
                adapter.actualizarCitas(it)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.cargarCitas()
    }
}

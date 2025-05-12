package com.univalle.dogapp.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.univalle.dogapp.R
import com.univalle.dogapp.model.Cita
import com.univalle.dogapp.databinding.ItemCitaBinding
import com.squareup.picasso.Picasso

class CitaAdapter(
    private var citas: List<Cita>,
    private val onItemClick: (Cita) -> Unit
) : RecyclerView.Adapter<CitaAdapter.CitaViewHolder>() {

    inner class CitaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemCitaBinding.bind(view)

        fun bind(cita: Cita) {
            binding.tvNombreMascota.text = cita.nombreMascota
            binding.tvSintoma.text = cita.sintomas
            binding.tvTurno.text = "#${cita.id}"

            if (cita.urlImagen.isNotEmpty()) {
                Picasso.get()
                    .load(cita.urlImagen)
                    .placeholder(R.drawable.ic_dog_placeholder)
                    .into(binding.ivMascota)
            }

            binding.root.setOnClickListener {
                onItemClick(cita)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cita, parent, false)
        return CitaViewHolder(view)
    }

    override fun getItemCount(): Int = citas.size

    override fun onBindViewHolder(holder: CitaViewHolder, position: Int) {
        holder.bind(citas[position])
    }

    fun actualizarCitas(nuevasCitas: List<Cita>) {
        citas = nuevasCitas
        notifyDataSetChanged()
    }
}

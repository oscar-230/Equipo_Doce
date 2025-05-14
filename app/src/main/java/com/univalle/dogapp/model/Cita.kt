package com.univalle.dogapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "citas")
data class Cita(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombreMascota: String,
    val raza: String,
    val propietario: String,
    val telefono: String,
    val sintomas: String,
    val urlImagen: String
)

package com.univalle.dogapp.repository

import com.univalle.dogapp.data.local.AppDatabase
import com.univalle.dogapp.model.Cita

class CitaRepository(private val db: AppDatabase) {

    suspend fun obtenerTodas(): List<Cita> {
        return try {
            db.citaDao().obtenerCitas().map { it.toModel() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun obtenerPorId(id: Int): Cita? {
        if (id <= 0) return null
        return try {
            db.citaDao().obtenerCitaPorId(id)?.toModel()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun eliminarPorId(id: Int) {
        if (id <= 0) return
        try {
            db.citaDao().eliminarCitaPorId(id)
        } catch (e: Exception) {
            // Handle error if needed
        }
    }

    suspend fun insertar(cita: Cita) {
        try {
            val entity = com.univalle.dogapp.model.Cita(
                nombreMascota = cita.nombreMascota,
                raza = cita.raza,
                propietario = cita.propietario,
                telefono = cita.telefono,
                sintomas = cita.sintomas,
                urlImagen = cita.urlImagen
            )
            db.citaDao().insertarCita(entity)
        } catch (e: Exception) {
            // Handle error if needed
        }
    }

    suspend fun actualizar(cita: Cita) {
        if (cita.id <= 0) return
        try {
            val entity = com.univalle.dogapp.model.Cita(
                id = cita.id,
                nombreMascota = cita.nombreMascota,
                raza = cita.raza,
                propietario = cita.propietario,
                telefono = cita.telefono,
                sintomas = cita.sintomas,
                urlImagen = cita.urlImagen
            )
            db.citaDao().actualizarCita(entity)
        } catch (e: Exception) {
            // Handle error if needed
        }
    }

    private fun com.univalle.dogapp.model.Cita.toModel(): Cita {
        return Cita(
            id = this.id,
            nombreMascota = this.nombreMascota,
            raza = this.raza,
            propietario = this.propietario,
            telefono = this.telefono,
            sintomas = this.sintomas,
            urlImagen = this.urlImagen
        )
    }
}

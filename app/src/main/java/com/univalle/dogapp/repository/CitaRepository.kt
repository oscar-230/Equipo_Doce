package com.univalle.dogapp.repository

import com.univalle.dogapp.data.local.AppDatabase
import com.univalle.dogapp.model.Cita

class CitaRepository(private val db: AppDatabase) {

    suspend fun obtenerTodas(): List<Cita> {
        return db.citaDao().obtenerCitas().map { it.toModel() }
    }

    suspend fun obtenerPorId(id: Int): Cita? {
        return db.citaDao().obtenerCitaPorId(id)?.toModel()
    }

    suspend fun eliminarPorId(id: Int) {
        db.citaDao().eliminarCitaPorId(id)
    }

    suspend fun insertar(cita: Cita) {
        val entity = com.univalle.dogapp.model.Cita(
            nombreMascota = cita.nombreMascota,
            raza = cita.raza,
            propietario = cita.propietario,
            telefono = cita.telefono,
            sintomas = cita.sintomas,
            urlImagen = cita.urlImagen
        )
        db.citaDao().insertarCita(entity)
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

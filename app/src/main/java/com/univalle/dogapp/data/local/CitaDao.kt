package com.univalle.dogapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CitaDao {
    @Insert
    suspend fun insertarCita(cita: CitaEntity)

    @Query("SELECT * FROM citas ORDER BY id DESC")
    suspend fun obtenerCitas(): List<CitaEntity>
}

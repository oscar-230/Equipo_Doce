package com.univalle.dogapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.univalle.dogapp.model.Cita

@Dao
interface CitaDao {
    @Insert
    suspend fun insertarCita(cita: Cita)

    @Query("SELECT * FROM citas ORDER BY id DESC")
    suspend fun obtenerCitas(): List<Cita>

    @Query("SELECT * FROM citas WHERE id = :id")
    suspend fun obtenerCitaPorId(id: Int): Cita?

    @Query("DELETE FROM citas WHERE id = :id")
    suspend fun eliminarCitaPorId(id: Int)

    @Update
    suspend fun actualizarCita(cita: Cita)
}

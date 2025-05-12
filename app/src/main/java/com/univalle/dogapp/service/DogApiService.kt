package com.univalle.dogapp.service

import com.univalle.dogapp.model.ImagenRazaResponse
import com.univalle.dogapp.model.RazasResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface DogApiService {
    @GET("breeds/list/all")
    suspend fun obtenerListaRazas(): RazasResponse

    @GET("breed/{raza}/images/random")
    suspend fun obtenerImagenRaza(@Path("raza") raza: String): ImagenRazaResponse
}

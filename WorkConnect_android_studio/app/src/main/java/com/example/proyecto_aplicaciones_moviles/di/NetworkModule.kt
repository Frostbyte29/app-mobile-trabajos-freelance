package com.example.proyecto_aplicaciones_moviles.di

import com.example.proyecto_aplicaciones_moviles.data.remote.WorkConnectApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    private const val BASE_URL = "https://jg1tqaanah.execute-api.us-east-1.amazonaws.com/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            // serializeNulls() NO está activado, así que Gson omite campos null
            // Esto evita que empresaInfo: null llegue al backend y falle la validación Zod
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val workConnectApi: WorkConnectApi by lazy {
        retrofit.create(WorkConnectApi::class.java)
    }
}
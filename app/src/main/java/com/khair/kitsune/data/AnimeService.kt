package com.khair.kitsune.data

import com.khair.kitsune.models.remote.Response
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface AnimeService {

    @GET("anime")
    fun getAnimes(): Call<Response>

    @GET("anime")
    fun getAnimes(@Query("filter[seasonYear]") seasonYear: String): Call<Response>
}
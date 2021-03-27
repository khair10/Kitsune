package com.khair.kitsune.data

import com.khair.kitsune.models.remote.Response
import retrofit2.Call
import retrofit2.http.GET

interface AnimeService {

    @GET("anime")
    fun getAnimes(): Call<Response>
}
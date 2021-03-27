package com.khair.kitsune.data

import retrofit2.Retrofit

class ServiceProvider(private val retrofit: Retrofit) {

    val animeService: AnimeService by lazy {
        retrofit.create(AnimeService::class.java)
    }
}
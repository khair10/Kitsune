package com.khair.kitsune.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors

class RetrofitHelper {

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://kitsu.io/api/edge/")
            .callbackExecutor(Executors.newSingleThreadExecutor())
            .build()
    }
}
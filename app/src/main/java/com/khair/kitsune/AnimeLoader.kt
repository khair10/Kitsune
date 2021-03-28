package com.khair.kitsune

import android.content.Context
import androidx.loader.content.AsyncTaskLoader
import com.khair.kitsune.data.RetrofitHelper
import com.khair.kitsune.data.ServiceProvider
import com.khair.kitsune.models.remote.AnimeResponse
import com.khair.kitsune.models.remote.Response
import retrofit2.Call

class AnimeLoader(context: Context, val query: String) : AsyncTaskLoader<List<AnimeResponse>>(context) {

    val retrofit = RetrofitHelper().retrofit
    val serviceProvider = ServiceProvider(retrofit)
    val animeService = serviceProvider.animeService
    var call: Call<Response>? = null

    companion object {
        const val TAG = "AnimeLoader"
    }

    override fun loadInBackground(): List<AnimeResponse>? {
        call = animeService.getAnimes(query)
        Thread.sleep(10000)
        return call?.execute()?.body()?.data
    }

    override fun onReset() {
        super.onReset()
        call?.cancel()
    }
}
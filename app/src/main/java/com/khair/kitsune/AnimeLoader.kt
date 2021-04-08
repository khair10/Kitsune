package com.khair.kitsune

import android.content.Context
import android.util.Log
import androidx.loader.content.AsyncTaskLoader
import com.khair.kitsune.data.RetrofitHelper
import com.khair.kitsune.data.ServiceProvider
import com.khair.kitsune.models.remote.AnimeResponse
import com.khair.kitsune.models.remote.Response
import retrofit2.Call
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class AnimeLoader(context: Context) : AsyncTaskLoader<List<AnimeResponse>>(context) {

    val retrofit = RetrofitHelper().retrofit
    val serviceProvider = ServiceProvider(retrofit)
    val animeService = serviceProvider.animeService
    var call: Call<Response>? = null
    var seasonYear = (Calendar.getInstance().get(Calendar.YEAR) - 2).toString()
    var data = emptyList<AnimeResponse>()
    val alreadyStarted = AtomicBoolean(false)

    companion object {
        const val TAG = "AnimeLoader"
    }

    override fun onStartLoading() {
        super.onStartLoading()
        if (data.isEmpty() && !alreadyStarted.get()) {
            forceLoad()
        }
    }

    fun query(query: String) {
        if (seasonYear != query) {
            seasonYear = query
            forceLoad()
        }
    }

    override fun loadInBackground(): List<AnimeResponse>? {
        alreadyStarted.compareAndSet(false, true)
        Log.d(TAG, "BACKGROUND, ${Thread.currentThread().name}")
        call = animeService.getAnimes(seasonYear)
        val data = call?.execute()?.body()?.data
        alreadyStarted.compareAndSet(true, false)
        return data
    }

    override fun onCancelLoad(): Boolean {
        Log.d(TAG, "onCancelLoad")
        alreadyStarted.compareAndSet(true, false)
        call?.cancel()
        return super.onCancelLoad()
    }

    override fun deliverResult(data: List<AnimeResponse>?) {
        Log.d(TAG, "Deliver")
        if (data == null) {
            this.data = emptyList()
        } else {
            this.data = data
        }
        super.deliverResult(this.data)
    }

    override fun onReset() {
        super.onReset()
        call?.cancel()
    }
}
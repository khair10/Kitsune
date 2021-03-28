package com.khair.kitsune

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import androidx.loader.content.Loader
import com.khair.kitsune.data.RetrofitHelper
import com.khair.kitsune.data.ServiceProvider
import com.khair.kitsune.models.remote.AnimeResponse
import com.khair.kitsune.models.remote.Response
import retrofit2.Call
import java.lang.ref.WeakReference

class AnimeLoader(context: Context, var query: String) : Loader<List<AnimeResponse>>(context) {

    private var task: AsyncTask<Void, Long, List<AnimeResponse>>? = null
    val retrofit = RetrofitHelper().retrofit
    val serviceProvider = ServiceProvider(retrofit)
    val animeService = serviceProvider.animeService

    companion object {
        const val TAG = "AnimeLoader"
    }

    override fun onStartLoading() {
        super.onStartLoading()
        Log.d(TAG, "onStartLoading + ${this.hashCode()}")
    }

    override fun onForceLoad() {
        super.onForceLoad()
        task?.cancel(true)
        task = AsyncLoad(this)
        task?.execute()
        Log.d(TAG, "onForceLoad")
    }

    override fun onStopLoading() {
        super.onStopLoading()
        Log.d(TAG, "onStopLoading")
    }

    override fun onAbandon() {
        super.onAbandon()
        Log.d(TAG, "onAbandon")
    }

    override fun onCancelLoad(): Boolean {
        Log.d(TAG, "onCancelLoad")
        return super.onCancelLoad()
    }

    override fun onReset() {
        super.onReset()
        task?.cancel(true)
        Log.d(TAG, "onReset + ${this.hashCode()}")
    }

    fun pushResult(list: List<AnimeResponse>?) {
        deliverResult(list)
    }

    class AsyncLoad(loader: AnimeLoader) : AsyncTask<Void, Long, List<AnimeResponse>>() {
        val loader = WeakReference(loader)

        var call: Call<Response>? = null

        override fun doInBackground(vararg params: Void?): List<AnimeResponse>? {
            call = loader.get()?.let { it.animeService.getAnimes(it.query) }
            val response = call?.execute()
            Thread.sleep(5000)
            Log.d("ASYNC_TASK", Thread.currentThread().name + " " + Thread.currentThread().id)
            Log.d("ASYNC_TASK", response?.body()?.links?.first ?: "empty")
            return response?.body()?.data
        }

        override fun onPostExecute(result: List<AnimeResponse>?) {
            super.onPostExecute(result)
            Log.d("ASYNC_TASK", Thread.currentThread().name + " " + Thread.currentThread().id)
            loader.get()?.pushResult(result)
        }

        override fun onCancelled() {
            super.onCancelled()
            call?.cancel()
            call = null
        }
    }
}
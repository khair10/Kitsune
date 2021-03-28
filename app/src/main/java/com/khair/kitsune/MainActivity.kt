package com.khair.kitsune

import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import com.khair.kitsune.data.RetrofitHelper
import com.khair.kitsune.data.ServiceProvider
import com.khair.kitsune.models.remote.Response
import retrofit2.Call
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity() {

    val retrofit = RetrofitHelper().retrofit
    val serviceProvider = ServiceProvider(retrofit)
    val animeService = serviceProvider.animeService
    var call: Call<Response>? = null
    var async: AsyncTask<Void, Long, String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("MainActivity", Thread.currentThread().name + ": on create")

        val tvText = findViewById<TextView>(R.id.tvText)

        call = animeService.getAnimes()
        call?.let {
            async = AsyncLoad(tvText, it)
        }
        async?.execute()
    }

    override fun onDestroy() {
        super.onDestroy()
        call?.cancel()
        async?.cancel(true)
    }

    class AsyncLoad(tvTextView: TextView, call: Call<Response>): AsyncTask<Void, Long, String>() {
        val tvTextView = WeakReference(tvTextView)
        val call = WeakReference(call)

        override fun doInBackground(vararg params: Void?): String {
            val response = call.get()?.execute()
            Thread.sleep(5000)
            Log.d("ASYNC_TASK", Thread.currentThread().name + " " + Thread.currentThread().id)
            return response?.body()?.data?.get(0)?.anime?.canonicalTitle ?: "Empty"
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            tvTextView.get()?.text = result
        }
    }
}
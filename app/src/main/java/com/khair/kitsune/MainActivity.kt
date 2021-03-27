package com.khair.kitsune

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.TextView
import androidx.core.os.HandlerCompat
import com.khair.kitsune.data.RetrofitHelper
import com.khair.kitsune.data.ServiceProvider
import com.khair.kitsune.models.remote.Response
import retrofit2.Call
import retrofit2.Callback
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity() {

    private lateinit var handler: Handler

    val retrofit = RetrofitHelper().retrofit
    val serviceProvider = ServiceProvider(retrofit)
    val animeService = serviceProvider.animeService
    var call: Call<Response>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("MainActivity", Thread.currentThread().name + ": on create")

        val tvText = findViewById<TextView>(R.id.tvText)

        handler = Handler(Looper.getMainLooper()) {
            Log.d("MainActivity", "message received")
            tvText.text = it.obj.toString()
            true
        }

        call = animeService.getAnimes()

        call?.enqueue(object : Callback<Response> {
            override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>) {
                handler.sendMessage(
                    handler.obtainMessage(
                        1,
                        response.body()?.data?.get(0)?.anime?.canonicalTitle
                    )
                )
            }

            override fun onFailure(call: Call<Response>, t: Throwable) {
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        call?.cancel()
        handler.removeCallbacksAndMessages(null)
    }
}
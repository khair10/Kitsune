package com.khair.kitsune

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.khair.kitsune.data.RetrofitHelper
import com.khair.kitsune.data.ServiceProvider
import com.khair.kitsune.models.remote.Response
import retrofit2.Call
import retrofit2.Callback

class MainActivity : AppCompatActivity() {

    val retrofit = RetrofitHelper().retrofit
    val serviceProvider = ServiceProvider(retrofit)
    val animeService = serviceProvider.animeService
    var call: Call<Response>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("MainActivity", Thread.currentThread().name + ": on create")

        val tvText = findViewById<TextView>(R.id.tvText)

        call = animeService.getAnimes()

        call?.enqueue(object : Callback<Response> {
            override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>) {
                Log.d("MainActivity", Thread.currentThread().name + ": on response")
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    for (item in body.data) {
                        Log.d("MainActivity", item.anime.toString())
                    }
                    tvText.text = body.data[0].anime.canonicalTitle
                }
            }

            override fun onFailure(call: Call<Response>, t: Throwable) {
                Log.d("MainActivity", t.localizedMessage)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        call?.cancel()
    }
}
package com.khair.kitsune

import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import com.khair.kitsune.data.RetrofitHelper
import com.khair.kitsune.data.ServiceProvider
import com.khair.kitsune.models.remote.Response
import retrofit2.Call
import java.lang.Exception
import java.lang.ref.WeakReference
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var handler: Handler
    private lateinit var executor: ExecutorService

    val retrofit = RetrofitHelper().retrofit
    val serviceProvider = ServiceProvider(retrofit)
    val animeService = serviceProvider.animeService
    var call: Call<Response>? = null
    var runner: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("MainActivity", Thread.currentThread().name + ": on create")

        val tvText = findViewById<TextView>(R.id.tvText)

        executor = Executors.newSingleThreadExecutor()
        handler = Handler(Looper.getMainLooper()) {
            Log.d("MainActivity", "message received")
            tvText.text = it.obj.toString()
            true
        }
        call = animeService.getAnimes()
        call?.let {
            runner = Runner(it, handler)
        }
        runner?.let {
            executor.submit(runner)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        call?.cancel()
        executor.shutdownNow()
        handler.removeCallbacksAndMessages(null)
    }

    class Runner(call: Call<Response>, handler: Handler) : Runnable {
        val call = WeakReference<Call<Response>>(call)
        val handler = WeakReference<Handler>(handler)

        override fun run() {
            Log.d("MainActivity", Thread.currentThread().name + ": on create")
            try {
                val data = call.get()?.execute()
                Thread.sleep(5000)
                handler.get()?.let {
                    it.sendMessage(
                        it.obtainMessage(
                            0,
                            data?.body()?.data?.get(0)?.anime?.canonicalTitle
                        )
                    )
                }
            } catch (e: Exception) {
                Log.d("MainActivity", e.localizedMessage)
            }
        }
    }
}
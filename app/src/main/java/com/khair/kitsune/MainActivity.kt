package com.khair.kitsune

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import android.widget.TextView
import com.khair.kitsune.data.RetrofitHelper
import com.khair.kitsune.data.ServiceProvider
import com.khair.kitsune.models.remote.Response
import retrofit2.Call
import retrofit2.Callback
import java.lang.Exception
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity() {

    private lateinit var handler: Handler
    private lateinit var workHandler: Handler
    private lateinit var handlerThread: HandlerThread

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

        handlerThread = HandlerThread("Background_executor")
        handlerThread.start()
        handler = Handler(Looper.getMainLooper()) {
            Log.d("MainActivity", "message received")
            tvText.text = it.obj.toString()
            true
        }
        workHandler = Handler(handlerThread.looper)
        call = animeService.getAnimes()
        call?.let {
            runner = Runner(it, handler)
        }
        runner?.let {
            workHandler.post(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        call?.cancel()
        workHandler.removeCallbacksAndMessages(null)
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
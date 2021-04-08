package com.khair.kitsune

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.util.SparseArray
import androidx.core.util.forEach
import com.khair.kitsune.data.RetrofitHelper
import com.khair.kitsune.data.ServiceProvider
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*

class ImageDownloadingService : Service() {

    companion object {
        const val NAME_KEY = "NAME"
        const val URL_KEY = "URL"
        const val TAG = "MyService"
    }

    val calls: SparseArray<Call<ResponseBody>> = SparseArray()
    val retrofit = RetrofitHelper().retrofit
    val serviceProvider = ServiceProvider(retrofit)
    val downloadService = serviceProvider.downloadService

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate, thread = ${Thread.currentThread().name}")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand : ${Thread.currentThread().name} : $startId")
        val url = intent?.getStringExtra(URL_KEY)
        val name = intent?.getStringExtra(NAME_KEY)
        if (url == null || name == null) {
            stopSelf(startId)
            return super.onStartCommand(intent, flags, startId)
        }
        val call = downloadService.download(url)
        Thread.sleep(5000)
        calls.put(startId, call)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                response.body()?.let{
                    Log.d(TAG, "onStartCommand Response: ${Thread.currentThread().name} : $startId")
                    writeFile(it, url, name)
                }
                calls.remove(startId)
                stopSelf(startId)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

            }
        })
        Log.d(TAG, "onStartCommand End : ${Thread.currentThread().name} : $startId")
        return START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
        calls.forEach { _, value -> value.cancel() }
    }

    fun writeFile(it: ResponseBody, url: String, name: String) {
        val imageName = name + url.split("/").last().split("?").first()
        val buffer = ByteArray(4096)
        val contentLength = it.contentLength()
        val byteInputStream = it.byteStream()
        Log.d("SERVICE", "name = $imageName, size = $contentLength, path = ${filesDir.absoluteFile}, thread = ${Thread.currentThread().name}")
        val outputStream = BufferedOutputStream(openFileOutput(imageName, MODE_PRIVATE))
        var batch = 0
        while (batch != -1) {
            batch = byteInputStream.read(buffer)
            outputStream.write(buffer)
        }
        outputStream.flush()
        try {
            byteInputStream.close()
        } catch (e: IOException) {
        }
        try {
            outputStream.close()
        }catch (e: IOException) {
        }
    }
}
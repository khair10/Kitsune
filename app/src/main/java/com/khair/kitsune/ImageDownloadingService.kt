package com.khair.kitsune

import android.app.IntentService
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

class ImageDownloadingService : IntentService("ImageDownloadingService") {

    companion object {
        const val NAME_KEY = "NAME"
        const val URL_KEY = "URL"
        const val TAG = "MyService"
    }

    val retrofit = RetrofitHelper().retrofit
    val serviceProvider = ServiceProvider(retrofit)
    val downloadService = serviceProvider.downloadService

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate, thread = ${Thread.currentThread().name}")
    }

    override fun onHandleIntent(intent: Intent?) {
        Log.d(TAG, "onStartCommand : ${Thread.currentThread().name}")
        val url = intent?.getStringExtra(URL_KEY)
        val name = intent?.getStringExtra(NAME_KEY)
        if (url == null || name == null) {
            return
        }
        val call = downloadService.download(url)
        Thread.sleep(5000)
        val reqBody = call.execute()
        val body = reqBody.body()
        body?.let {
            writeFile(body, url, name)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
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
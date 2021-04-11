package com.khair.kitsune

import android.content.Context
import android.content.Intent
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import com.khair.kitsune.models.remote.AnimeResponse
import java.lang.RuntimeException
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val LOADER_ID = 0
    }

    private var loaderCallback: AnimeLoaderCallback? = null
    private lateinit var tvText: TextView
    private lateinit var btn2000: Button
    private lateinit var btn2015: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("MAINACTIVITY", "onCreate, thread = ${Thread.currentThread().name}")
        tvText = findViewById(R.id.tvText)
        btn2000 = findViewById<Button>(R.id.btn2000).apply {
            setOnClickListener(this@MainActivity)
        }
        btn2015 = findViewById<Button>(R.id.btn2015).apply {
            setOnClickListener(this@MainActivity)
        }

        loaderCallback = AnimeLoaderCallback(this, tvText)
        loaderCallback?.let {
            supportLoaderManager.initLoader(
                LOADER_ID,
                null,
                it
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onClick(v: View?) {
        val season = when (v?.id) {
            R.id.btn2000 -> "2000"
            R.id.btn2015 -> "2015"
            else -> return
        }
        val loader = supportLoaderManager.getLoader<AnimeLoader>(LOADER_ID) as AnimeLoader
        loader.query(season)
    }

    class AnimeLoaderCallback(context: Context, tvText: TextView) :
        LoaderManager.LoaderCallbacks<List<AnimeResponse>> {
        val context = WeakReference(context)
        val tvText = WeakReference(tvText)

        override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<AnimeResponse>> {
            Log.d(AnimeLoader.TAG, "CALLBACK: onCreateLoader")
            context.get()?.let {
                return when (id) {
                    LOADER_ID -> AnimeLoader(context = it)
                    else -> throw RuntimeException("Empty context")
                }
            }
            throw RuntimeException("Empty context")
        }

        override fun onLoadFinished(
            loader: Loader<List<AnimeResponse>>,
            data: List<AnimeResponse>?
        ) {
            Log.d(AnimeLoader.TAG, "CALLBACK: onLoadFinished, $data")
            val text = if (data != null && data.isNotEmpty()) {
                context.get()?.let {context ->
                    data.forEach {animeResponse ->
                        ImageDownloadingService.enqueueWork(context, Intent(context, ImageDownloadingService::class.java)
                            .apply {
                                putExtra(ImageDownloadingService.URL_KEY, animeResponse.anime.image.tiny)
                                putExtra(ImageDownloadingService.NAME_KEY, animeResponse.anime.canonicalTitle)
                            }
                        )
                    }
                }
                data[0].anime.canonicalTitle
            } else {
                "Empty"
            }
            tvText.get()?.text = text
        }

        override fun onLoaderReset(loader: Loader<List<AnimeResponse>>) {
            Log.d(AnimeLoader.TAG, "CALLBACK: onLoaderReset")
        }
    }
}
package com.khair.kitsune.data

import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.khair.kitsune.models.remote.Anime
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.util.concurrent.Executors

class RetrofitHelper {

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().registerTypeAdapter(
                        Anime::class.java,
                        object : TypeAdapter<Anime>() {
                            override fun write(out: JsonWriter?, value: Anime?) {
                                out?.let {
                                    it.beginObject()
                                        .name("canonicalTitle").value(value?.canonicalTitle)
                                        .name("averageRating").value(value?.averageRating)
                                        .name("synopsis").value(value?.synopsis)
                                        .endObject()
                                }
                            }

                            override fun read(reader: JsonReader?): Anime {
                                return if (reader != null) {
                                    var canonicalTitle = ""
                                    var averageRating = ""
                                    var synopsis = ""
                                    reader.beginObject()
                                    while (reader.hasNext()) {
                                        val name = reader.nextName()
                                        when {
                                            name.equals("canonicalTitle") -> {
                                                canonicalTitle = reader.nextString()
                                            }
                                            name.equals("averageRating") -> {
                                                averageRating = reader.nextString()
                                            }
                                            name.equals("synopsis") -> {
                                                synopsis = reader.nextString()
                                            }
                                            else -> {
                                                reader.skipValue()
                                            }
                                        }
                                    }
                                    reader.endObject()
                                    Anime(canonicalTitle + 1, averageRating + 2, synopsis + 3)
                                } else {
                                    Anime("Empty", "Empty", "Empty")
                                }
                            }
                        }).create()
                )
            )
            .baseUrl("https://kitsu.io/api/edge/")
            .callbackExecutor(Executors.newSingleThreadExecutor())
            .build()
    }
}
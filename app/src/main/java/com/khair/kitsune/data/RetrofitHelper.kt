package com.khair.kitsune.data

import com.google.gson.*
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
                        object: JsonDeserializer<Anime> {
                            override fun deserialize(
                                json: JsonElement?,
                                typeOfT: Type?,
                                context: JsonDeserializationContext?
                            ): Anime {
                                var name = "Empty"
                                var average = "Empty"
                                var synopsis = "Empty"
                                json?.let {
                                    val jsonObj = it.asJsonObject
                                    name = jsonObj["canonicalTitle"].asString + 1
                                    average = jsonObj["averageRating"].asString + 2
                                    synopsis = jsonObj["synopsis"].asString + 3
                                }
                                return Anime(name, average, synopsis)
                            }
                        })
                        .create()
                )
            )
            .baseUrl("https://kitsu.io/api/edge/")
            .callbackExecutor(Executors.newSingleThreadExecutor())
            .build()
    }
}
package com.khair.kitsune.data

import com.google.gson.*
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.khair.kitsune.models.remote.Anime
import com.khair.kitsune.models.remote.Image
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
                                var image = Image("Empty")
                                json?.let {
                                    val jsonObj = it.asJsonObject
                                    name = if (jsonObj["canonicalTitle"].isJsonPrimitive) jsonObj["canonicalTitle"].asString + 1 else 1.toString()
                                    average = if (jsonObj["averageRating"].isJsonPrimitive) jsonObj["averageRating"].asString + 2 else 2.toString()
                                    synopsis = if (jsonObj["synopsis"].isJsonPrimitive) jsonObj["synopsis"].asString + 3 else 3.toString()
                                    image = if (context != null) context.deserialize(jsonObj["posterImage"], Image::class.java) else image
                                }
                                return Anime(name, average, synopsis, image)
                            }
                        })
                        .serializeNulls()
                        .create()
                )
            )
            .baseUrl("https://kitsu.io/api/edge/")
//            .callbackExecutor(Executors.newSingleThreadExecutor())
            .build()
    }
}
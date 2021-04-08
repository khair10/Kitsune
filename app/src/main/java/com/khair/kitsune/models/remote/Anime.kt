package com.khair.kitsune.models.remote

import com.google.gson.annotations.SerializedName

data class Anime(
    @SerializedName("canonicalTitle") val canonicalTitle: String,
    @SerializedName("averageRating") val averageRating: String,
    @SerializedName("synopsis") val synopsis: String,
    @SerializedName("posterImage") val image: Image
)

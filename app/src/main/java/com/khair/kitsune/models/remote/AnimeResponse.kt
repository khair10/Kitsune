package com.khair.kitsune.models.remote

import com.google.gson.annotations.SerializedName

data class AnimeResponse(
    @SerializedName("attributes") val anime: Anime
)

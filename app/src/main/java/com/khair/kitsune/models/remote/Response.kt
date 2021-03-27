package com.khair.kitsune.models.remote

import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("data") val data: List<AnimeResponse>
)

package com.khair.kitsune.models.remote

import com.google.gson.annotations.SerializedName

data class Image(
    @SerializedName("tiny") val tiny: String
)
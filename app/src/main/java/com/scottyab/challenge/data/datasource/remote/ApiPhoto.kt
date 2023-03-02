package com.scottyab.challenge.data.datasource.remote

import com.squareup.moshi.Json

data class ApiPhoto(
    val id: String,
    val title: String,
    @Json(name = "url_c")
    val imageUrl: String
)

package com.scottyab.challenge.data.datasource.remote

import com.scottyab.challenge.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.IOException

interface FlickrService {
    @Throws(IOException::class)
    @Suppress("LongParameterList")
    @GET("services/rest/?method=flickr.photos.search&nojsoncallback=1&format=json")
    suspend fun searchPhotos(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("api_key") apiKey: String = BuildConfig.FLICKR_API_KEY,
        @Query("safe_search") safeSearch: Int = 1, // safe
        @Query("content_types") contentTypes: Int = 0, // photos
        @Query("radius") radius: Double = 0.1, // 100m radius (note radius_units defaults to KM)
        @Query("extras") extras: String = "url_c",
        @Query("per_page") perPage: Int = 10
    ): ApiPhotosResponse
}

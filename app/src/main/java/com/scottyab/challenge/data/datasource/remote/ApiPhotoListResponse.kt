package com.scottyab.challenge.data.datasource.remote

data class ApiPhotosResponse(val photos: ApiPhotosList)

data class ApiPhotosList(val page: Int, val pages: Int, val total: String, val photo: List<ApiPhoto>)

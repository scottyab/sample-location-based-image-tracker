package com.scottyab.challenge.domain.mappers

import com.scottyab.challenge.data.datasource.remote.ApiPhoto
import com.scottyab.challenge.domain.model.Photo

class PhotoMapper {

    fun toDomain(api: ApiPhoto) = Photo(id = api.id, imageUrl = api.imageUrl, title = api.title)
}

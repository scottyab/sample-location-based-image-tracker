package com.scottyab.challenge.presentation.common

import android.widget.ImageView
import coil.load
import com.scottyab.challenge.R

// insulate the app from Image loading library changes
interface ImageLoader {

    fun load(url: String, target: ImageView)
}

/**
 * TODO Actually configure the Coil's ImageLoader i.e cache settings so the app is using our custom
 *  one not the default singleton
 */
class CoilImageLoader : ImageLoader {

    override fun load(url: String, target: ImageView) {
        target.load(url) {
            placeholder(R.drawable.ic_image_load_fail_placeholder)
            error(R.drawable.ic_image_load_fail_placeholder)
        }
    }
}

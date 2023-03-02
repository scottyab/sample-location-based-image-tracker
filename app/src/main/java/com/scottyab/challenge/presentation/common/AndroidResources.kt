package com.scottyab.challenge.presentation.common

import android.content.Context
import androidx.annotation.StringRes

interface AndroidResources {

    /**
     * Returns a localized string from the application's package's default string table.
     *
     * @param resId Resource id for the string
     * @return The string data associated with the resource, stripped of styled text information.
     */
    fun string(@StringRes resId: Int): String
}

class AndroidResourcesProvider(context: Context) : AndroidResources {

    private val appContext = context.applicationContext

    override fun string(@StringRes resId: Int): String {
        return appContext.getString(resId)
    }
}

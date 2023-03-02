package com.scottyab.challenge.presentation.common

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

/**
 * Activity binding delegate, may be used since onCreate up to onDestroy (inclusive)
 * from https://gist.github.com/gmk57/aefa53e9736d4d4fb2284596fb62710d
 */
inline fun <T : ViewBinding> FragmentActivity.viewBinding(
    crossinline factory: (LayoutInflater) -> T
) = lazy(LazyThreadSafetyMode.NONE) { factory(layoutInflater) }

/**
 *
 * @return Can return null if [view] is `null` and the `rootView` cannot be found
 */
fun Activity.snack(
    message: String,
    isAnchor: Boolean = false,
    length: Int = Snackbar.LENGTH_LONG,
    view: View? = null,
    @BaseTransientBottomBar.AnimationMode animationMode: Int = BaseTransientBottomBar.ANIMATION_MODE_SLIDE,
    f: Snackbar.() -> Unit = {}
): Snackbar? {
    val v = view ?: window.decorView.rootView ?: return null
    return v.snack(message, length, isAnchor, animationMode, f)
}

/**
 * @return a share intent with the message
 */
fun Activity.createShareIntent(message: String): Intent {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, message)
        type = "text/plain"
    }
    return Intent.createChooser(sendIntent, null)
}

fun Activity.openAppSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
        .apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    startActivity(intent)
}

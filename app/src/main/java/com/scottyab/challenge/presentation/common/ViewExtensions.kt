package com.scottyab.challenge.presentation.common

import android.view.View
import androidx.annotation.ColorInt
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

fun View.throttledClick(throttleTime: Long = 1000L, action: (View) -> Unit) {
    val throttler = SimpleThrottler(throttleTime)
    setOnClickListener { throttler.throttle { action(this) } }
}

/**
 * How to use:
 *
 * ```
 * view.snack("Snack message") {
 *     action("Action") { toast("Action clicked") }
 * }
 * ``
 */
inline fun View.snack(
    message: String,
    length: Int = Snackbar.LENGTH_LONG,
    isAnchor: Boolean = false,
    @BaseTransientBottomBar.AnimationMode animationMode: Int = BaseTransientBottomBar.ANIMATION_MODE_SLIDE,
    f: Snackbar.() -> Unit = {}
): Snackbar {
    val view = this
    return Snackbar.make(view, message, length).apply {
        if (isAnchor) anchorView = view
        setAnimationMode(animationMode)
        f()
    }.also(Snackbar::show)
}

fun Snackbar.action(action: String, @ColorInt color: Int? = null, listener: (View) -> Unit) {
    setAction(action, listener)
    color?.let { setActionTextColor(color) }
}

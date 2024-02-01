package com.scottyab.challenge.presentation.common

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Use for the propagation of UI event, such as Click so the click is not retriggered on
 * configuration change
 *
 * From https://abhiappmobiledeveloper.medium.com/android-singleliveevent-of-livedata-for-ui-event-35d0c58512da
 */
class SingleLiveEvent<T> : MutableLiveData<T>() {
    private val mPending = AtomicBoolean(false)

    @MainThread
    override fun observe(
        owner: LifecycleOwner,
        observer: Observer<in T>,
    ) {
        if (hasActiveObservers()) {
            Timber.w(TAG, "Multiple observers registered but only one will be notified of changes.")
        } // Observe the internal MutableLiveData
        super.observe(owner) { t ->
            if (mPending.compareAndSet(true, false)) {
                observer.onChanged(t)
            }
        }
    }

    @MainThread
    override fun setValue(t: T?) {
        mPending.set(true)
        super.setValue(t)
    }

    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @MainThread
    fun call() {
        setValue(null)
    }

    companion object {
        private val TAG = "SingleLiveEvent"
    }
}

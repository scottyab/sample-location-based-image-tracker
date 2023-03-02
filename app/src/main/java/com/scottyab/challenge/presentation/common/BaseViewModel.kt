package com.scottyab.challenge.presentation.common

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.jetbrains.annotations.TestOnly

abstract class BaseViewModel<S : Any>(initialState: S) : ViewModel() {

    private val _state: MutableLiveData<S> = MutableLiveData(initialState)

    val state: LiveData<S> get() = _state

    @TestOnly
    @MainThread
    fun setState(reducer: S.() -> S) {
        val currentState = _state.value!!
        val newState = currentState.reducer()
        if (newState != currentState) {
            _state.value = newState
        }
    }
}

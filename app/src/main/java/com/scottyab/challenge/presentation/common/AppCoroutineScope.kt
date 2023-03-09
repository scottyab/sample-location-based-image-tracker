package com.scottyab.challenge.presentation.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.jetbrains.annotations.TestOnly
import kotlin.coroutines.CoroutineContext

/**
 * CoroutineScope associated to the Application lifecycle.
 * No need to cancel this scope as it'll be torn down with the process
 *
 * By this scope is bound to
 * [Dispatchers.IO][kotlinx.coroutines.Dispatchers.IO] (can be overridden for testing)
 */
class AppCoroutineScope @TestOnly constructor(
    override val coroutineContext: CoroutineContext = Dispatchers.IO
) : CoroutineScope {

    constructor(dispatcher: CoroutineDispatcher) : this(
        coroutineContext = SupervisorJob() + dispatcher
    )
}

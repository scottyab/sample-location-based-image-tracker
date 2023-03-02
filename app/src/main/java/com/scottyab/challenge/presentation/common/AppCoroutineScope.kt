package com.scottyab.challenge.presentation.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.jetbrains.annotations.TestOnly
import kotlin.coroutines.CoroutineContext

/**
 * CoroutineScope associated to the Application lifecycle.
 * No need to cancel this scope as it'll be torn down with the process
 *
 * This scope is bound to
 * [Dispatchers.IO][kotlinx.coroutines.Dispatchers.IO]
 */
class AppCoroutineScope @TestOnly constructor(
    override val coroutineContext: CoroutineContext
) : CoroutineScope {

    constructor(dispatcher: CoroutineDispatcher) : this(
        coroutineContext = SupervisorJob() + dispatcher
    )
}

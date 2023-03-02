package com.scottyab.challenge

import org.assertj.core.api.AbstractObjectAssert

@Suppress("UNCHECKED_CAST")
inline fun <reified R> AbstractObjectAssert<*, *>.isNotInstanceOf(): AbstractObjectAssert<*, *>? =
    isNotInstanceOf(R::class.java)

@Suppress("UNCHECKED_CAST")
inline fun <reified R> AbstractObjectAssert<*, *>.isInstanceOf(): AbstractObjectAssert<*, *>? =
    isInstanceOf(R::class.java)

@Suppress("UNCHECKED_CAST")
inline fun <reified R> AbstractObjectAssert<*, *>.isInstanceOf(
    crossinline requirements: (R) -> Unit
): AbstractObjectAssert<*, *>? =
    isInstanceOfSatisfying(R::class.java) { requirements(it) }

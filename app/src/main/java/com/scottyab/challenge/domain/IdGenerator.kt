package com.scottyab.challenge.domain

import java.util.UUID

interface IdGenerator {
    fun generate(): String
}

class UUIDGenerator : IdGenerator {
    override fun generate() = UUID.randomUUID().toString()
}

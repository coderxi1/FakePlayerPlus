package com.coderxi.plugin.fakeplayer.scope

import com.coderxi.plugin.fakeplayer.entity.FakePlayer
import java.util.UUID

interface FakePlayerScope {

    val uniqueId: UUID

    fun fakeplayers(): Collection<FakePlayer>

    fun spawn(name: String): FakePlayer

    fun tick()

    fun remove(uuid: UUID)

    fun destroy()

}
package com.coderxi.plugin.fakeplayer.config

import com.coderxi.plugin.fakeplayer.provider.invsee.InvseeProvider
import com.coderxi.plugin.fakeplayer.provider.invsee.OpenInvInvseeProvider
import com.coderxi.plugin.fakeplayer.provider.invsee.VanillaInvseeProvider

enum class DeathEventAction { NONE, QUIT, RESPAWN, RESPAWN_BACK }

enum class InvseeProviderType(val providerClass: Class<out InvseeProvider>) {
    VANILLA(VanillaInvseeProvider::class.java),
    OPENINV(OpenInvInvseeProvider::class.java)
}
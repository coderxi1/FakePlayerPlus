package com.coderxi.plugin.fakeplayer.config

import com.coderxi.plugin.fakeplayer.provider.invsee.*

enum class OnDeathAction { NONE, QUIT, RESPAWN, RESPAWN_BACK }

enum class InvseeProviderType(val providerClass: Class<out InvseeProvider>) {
    VANILLA(VanillaInvseeProvider::class.java),
    OPENINV(OpenInvInvseeProvider::class.java)
}
package com.coderxi.plugin.fakeplayer.api.nms

import java.util.UUID

interface NMSServer {

    fun newPlayer(uuid: UUID, name: String): NMSServerPlayer

}

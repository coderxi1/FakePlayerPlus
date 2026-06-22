package com.coderxi.plugin.fakeplayer.command.annotaion

import com.coderxi.plugin.fakeplayer.utils.PluginComponent
import revxrsal.commands.Lamp
import revxrsal.commands.annotation.list.AnnotationList
import revxrsal.commands.bukkit.actor.BukkitCommandActor
import revxrsal.commands.command.CommandPermission

class PluginCommandPermissionFactory : CommandPermission.Factory<BukkitCommandActor>, PluginComponent {
    override fun create(
        annotations: AnnotationList,
        lamp: Lamp<BukkitCommandActor?>
    ): CommandPermission<BukkitCommandActor?>? {
        val permissionAnno = annotations.get(PluginCommandPermission::class.java) ?: return null
        val permissionNode = permissionAnno.node.value
        val permissionNodeOr = permissionAnno.or.value
        return CommandPermission { actor ->
            val sender = actor.sender()
            sender.hasPermission(permissionNode) || sender.hasPermission(permissionNodeOr)
        }
    }

}
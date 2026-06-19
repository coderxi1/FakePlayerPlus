package com.coderxi.plugin.fakeplayer.command.annotaion

import com.coderxi.plugin.fakeplayer.command.permission.Permission

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class PluginPermission(val node: Permission, val or: Permission = Permission.ADMIN)
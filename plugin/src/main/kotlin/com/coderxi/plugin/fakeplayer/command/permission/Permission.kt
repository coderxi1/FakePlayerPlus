package com.coderxi.plugin.fakeplayer.command.permission

enum class Permission(val value: String) {

    ADMIN("fakeplayer.admin"),
    BASIC("fakeplayer.basic"),

    RELOAD("fakeplayer.reload"),

    SPAWN("fakeplayer.spawn"),
    SPAWN_WITH_NAME("fakeplayer.spawn.name"),
    SPAWN_LIMIT_CUSTOM("fakeplayer.spawn.limit.{node}"),

    SELECT("fakeplayer.select"),

    REMOVE("fakeplayer.remove"),
    INVSEE("fakeplayer.invsee"),
    TP("fakeplayer.tp"),
    SKIN("fakeplayer.skin"),
    CMD("fakeplayer.command"),
    CHAT("fakeplayer.chat"),

    SETTINGS("fakeplayer.settings"),

}
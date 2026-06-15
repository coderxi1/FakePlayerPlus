package com.coderxi.plugin.fakeplayer.command.exception

import revxrsal.commands.exception.CommandErrorException

abstract class FakePlayerCommandException : CommandErrorException() {

    class NotExits(val name: String) : FakePlayerCommandException()

}
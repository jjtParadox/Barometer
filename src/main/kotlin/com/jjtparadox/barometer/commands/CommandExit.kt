/*
 * This file is part of Barometer
 *
 * Copyright (c) 2018 jjtParadox
 *
 * Barometer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Barometer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Barometer. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jjtparadox.barometer.commands

import com.jjtparadox.barometer.Barometer
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.server.MinecraftServer

/**
 * Command for stopping the server without saving any worlds
 */
class CommandExit : CommandBase() {

    override fun getName(): String {
        return "exit"
    }

    override fun getUsage(sender: ICommandSender?): String {
        return "exit"
    }

    override fun execute(server: MinecraftServer?, sender: ICommandSender?, args: Array<out String>?) {
        Barometer.instance?.shutdown()
    }

}

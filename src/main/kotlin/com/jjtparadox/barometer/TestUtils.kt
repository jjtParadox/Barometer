/*
 * This file is part of Barometer
 *
 * Copyright (c) 2017 jjtParadox
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
package com.jjtparadox.barometer

import com.jjtparadox.barometer.experimental.env.TestWorldSaveHandler
import com.jjtparadox.barometer.experimental.env.TestWorldServer
import net.minecraft.world.DimensionType
import net.minecraft.world.GameType
import net.minecraft.world.WorldSettings
import net.minecraft.world.WorldType
import net.minecraft.world.storage.WorldInfo
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.fml.server.FMLServerHandler

object TestUtils {
    @JvmStatic fun tickServer() {
        //TODO tick the server in a way that doesn't autosave unless requested
        Barometer.server.tick()
    }

    @JvmStatic fun createEmptyWorld(name: String): TestWorldServer {
        TODO("Not fully implemented!")

        val settings = WorldSettings(0, GameType.SURVIVAL, false, false, WorldType.FLAT)
        val info = WorldInfo(settings, name)
        val handler = TestWorldSaveHandler(info)

        try {
            DimensionManager.unregisterDimension(0)
        } catch (e: Exception) {
        }
        DimensionManager.registerDimension(0, DimensionType.valueOf("BarometerWorld"))
        return TestWorldServer(FMLServerHandler.instance().server, handler, info, 0)
    }
}

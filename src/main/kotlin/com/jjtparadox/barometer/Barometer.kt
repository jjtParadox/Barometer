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

import com.google.common.collect.Queues
import com.jjtparadox.barometer.tester.BarometerTester
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner
import net.minecraft.launchwrapper.Launch
import net.minecraft.server.dedicated.DedicatedServer
import net.minecraft.server.dedicated.PropertyManager
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraftforge.common.ForgeChunkManager
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent
import net.minecraftforge.fml.common.event.FMLServerStartedEvent
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.junit.runner.RunWith
import java.io.File
import java.util.Queue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.FutureTask

val LOGGER: Logger = LogManager.getLogger(Barometer.MOD_ID)

@Mod(modid = Barometer.MOD_ID, version = Barometer.VERSION, serverSideOnly = true)
class Barometer {
    companion object {
        const val MOD_ID = "barometer"
        const val VERSION = "0.0.2-1.12"

        @JvmField val futureTaskQueue: Queue<FutureTask<*>> = Queues.newArrayDeque<FutureTask<*>>()
        @JvmField var testing = true
        @JvmField var finishedLatch = CountDownLatch(1)

        @JvmStatic val server by lazy { theServer } // Hack to create a lateinit val
        private lateinit var theServer: DedicatedServer

        // Use reflection to find the number of Barometer tests being run
        @JvmStatic
        fun getTestCount(): Int {
            var numTests = 0;
            // Only scan directories for .class files
            var scanner = FastClasspathScanner("-jar:")
            scanner.matchClassesWithAnnotation(RunWith::class.java,
                    { c -> run {
                        val value = c.getAnnotation(RunWith::class.java).value
                        if ( value == BarometerTester::class )
                            numTests++
                    } })
            scanner.scan()
            return numTests
        }
    }

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        if (Launch.classLoader.getClassBytes("net.minecraft.world.World") == null) {
//            LOGGER.error("Barometer should only be used in a deobsfucated dev environment!")
//            Loader.instance().activeModContainer().setEnabledState(false)
            throw IllegalStateException("The Barometer mod should only be used in a deobsfucated test environment!")
        }

        theServer = FMLCommonHandler.instance().minecraftServerInstance as DedicatedServer
        val serverSettings = PropertyManager(File("server.properties"))

        // Use safeSet so as to preserve any existing server.properties file
        safeSet(serverSettings, "online-mode", false)
        safeSet(serverSettings,"server-ip", "127.0.0.1")
        safeSet(serverSettings,"spawn-animals", false)
        safeSet(serverSettings,"spawn-npcs", false)
        safeSet(serverSettings,"motd", "Barometer Test Server")
        safeSet(serverSettings,"force-gamemode", true)
        safeSet(serverSettings,"difficulty", 0)
        safeSet(serverSettings,"generate-structures", false)
        safeSet(serverSettings,"gamemode", 0)
        safeSet(serverSettings,"level-type", "FLAT")
        safeSet(serverSettings,"generator-settings", "3;minecraft:air;127;")
        safeSet(serverSettings,"max-tick-time", 0)
        serverSettings.saveProperties()

        server.serverOwner = "barometer_test_player"

        MinecraftForge.EVENT_BUS.register(this)
    }

    private fun safeSet(settings: PropertyManager, key: String, value: Any) {
        if ( !settings.hasProperty(key) )
            settings.setProperty(key, value)
    }

    @Mod.EventHandler
    fun serverAboutToStart(event: FMLServerAboutToStartEvent) {
    }

    @Mod.EventHandler
    fun serverStarted(event: FMLServerStartedEvent) {
        while (testing) {
            synchronized(futureTaskQueue) {
                futureTaskQueue.forEach { it.run() }
            }
        }
        endTesting()
    }

    @Mod.EventHandler
    fun serverStopped(event: FMLServerStoppedEvent) {
        finishedLatch.countDown()
    }

    // Clear all worlds and shut down the server
    private fun endTesting() {
        server.worlds = null
        server.initiateShutdown()
    }

    // Set world spawn to origin and add a loaded chunk so the world ticks without needing a player
    @SubscribeEvent
    fun onWorldLoad(event: WorldEvent.Load) {
        event.world.spawnPoint = BlockPos.ORIGIN

        ForgeChunkManager.setForcedChunkLoadingCallback(this, { _, _ -> })
        val ticket = ForgeChunkManager.requestTicket(this, event.world, ForgeChunkManager.Type.NORMAL)
        ForgeChunkManager.forceChunk(ticket, ChunkPos(BlockPos.ORIGIN))
    }
}

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
package com.jjtparadox.barometer.tester

import GradleStartServer
import com.google.common.util.concurrent.ListenableFutureTask
import com.jjtparadox.barometer.Barometer
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner
import net.minecraft.launchwrapper.Launch
import net.minecraft.launchwrapper.LaunchClassLoader
import net.minecraftforge.fml.common.launcher.FMLServerTweaker
import org.junit.runner.RunWith
import org.junit.runner.notification.RunNotifier
import org.junit.runners.BlockJUnit4ClassRunner
import org.junit.runners.model.FrameworkMethod
import org.junit.runners.model.InitializationError
import java.util.Queue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.FutureTask

class BarometerTester(klass: Class<*>) : BlockJUnit4ClassRunner(load(klass)) {
    @Suppress("UNCHECKED_CAST")
    companion object {
        var started = false
        var testCount = -1

        // References to Barometer's mod class & fields
        lateinit var barometer: Class<*>
        lateinit var taskQueue: Queue<FutureTask<*>>
        lateinit var finishedLatch: CountDownLatch

        private fun load(klass: Class<*>): Class<*> {
            // If game not started, start it and grab the Barometer mod class
            if (!started) {
                started = true

                // Normally, LaunchWrapper sets the thread's context class loader to the LaunchClassLoader.
                // However, that causes issue as soon as tests are run in the normal class loader in the same thread.
                // Simply resetting it seems to fix various issues with Mockito.

                val thread = Thread.currentThread()
                val contextClassLoader = thread.contextClassLoader

                GradleStartTestServer().launch(arrayOf("--noCoreSearch", "nogui"))

                thread.contextClassLoader = contextClassLoader

                barometer = Class.forName(Barometer::class.qualifiedName, true, Launch.classLoader)
                taskQueue = barometer.getField("futureTaskQueue")[null] as Queue<FutureTask<*>>
                finishedLatch = barometer.getField("finishedLatch")[null] as CountDownLatch
            }

            // Return test class that's been loaded by the same classloader as Minecraft
            try {
                return Class.forName(klass.name, true, Launch.classLoader)
            } catch (e: ClassNotFoundException) {
                throw InitializationError(e)
            }
        }
    }

    override fun run(notifier: RunNotifier?) {
        super.run(notifier)
        if (testCount == -1) {
            try {
                // Use FastClasspathScanner to find the number of Barometer tests being run
                testCount = 0
                // Only scan directories for .class files
                val scanner = FastClasspathScanner("-jar:")
                scanner.matchClassesWithAnnotation(RunWith::class.java, {
                    val value = it.getAnnotation(RunWith::class.java).value
                    if (value == BarometerTester::class)
                        testCount++
                }
                )
                scanner.scan()
            } catch (e: Exception) {
                System.err.println("Could not get testCount:")
                e.printStackTrace(System.err)
            }
        }
        testCount--
        if (testCount <= 0) {
            barometer.getField("testing").setBoolean(null, false)
            finishedLatch.await()
        }
    }

    override fun runChild(method: FrameworkMethod?, notifier: RunNotifier?) {
        val task = ListenableFutureTask.create {
            super.runChild(method, notifier)
        }
        synchronized(taskQueue) {
            taskQueue.add(task)
        }
        task.get()
    }
}

class GradleStartTestServer : GradleStartServer() {
    public override fun launch(args: Array<String>) {
        super.launch(args)
    }

    override fun getTweakClass(): String? {
        return TestTweaker::class.qualifiedName
    }
}

class TestTweaker : FMLServerTweaker() {

    override fun injectIntoClassLoader(classLoader: LaunchClassLoader) {
        classLoader.addTransformerExclusion("com.jjtparadox.barometer.experimental.env.")

        classLoader.addClassLoaderExclusion("junit.")
        classLoader.addClassLoaderExclusion("org.junit.")
        classLoader.addClassLoaderExclusion("org.hamcrest.")

        classLoader.addClassLoaderExclusion("org.mockito.")
        classLoader.addClassLoaderExclusion("net.bytebuddy.")
        classLoader.addClassLoaderExclusion("org.objenesis.")

        classLoader.addClassLoaderExclusion("org.easymock.")
        classLoader.addClassLoaderExclusion("cglib.")
        classLoader.addClassLoaderExclusion("org.testng.")

        classLoader.addClassLoaderExclusion("org.powermock.")
        classLoader.addClassLoaderExclusion("org.javassist.")
        classLoader.addClassLoaderExclusion("com.thoughtworks.xstream")

        super.injectIntoClassLoader(classLoader)
    }
}

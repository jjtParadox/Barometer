package com.jjtparadox.barometer.test

import com.jjtparadox.barometer.Barometer
import com.jjtparadox.barometer.TestUtils
import com.jjtparadox.barometer.tester.BarometerTester
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertSame
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntityFurnace
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(BarometerTester::class)
class BarometerExampleTest {
    @Test
    fun testThatThisHackWorks() {
        println("One test successful!")
    }

    lateinit var world: World

    @Before
    fun setUp() {
        world = Barometer.server.entityWorld
    }

    @Test
    fun testFurnacePlacement() {
        if (Blocks.FURNACE != null) {
            val pos = BlockPos.ORIGIN
            world.setBlockState(pos, Blocks.FURNACE.defaultState)
            val state = world.getBlockState(pos)
            val block = state.block
            assertEquals(block, Blocks.FURNACE)
            val tile = world.getTileEntity(pos)
            assertSame(tile?.javaClass, TileEntityFurnace::class.java)
        } else {
            fail("Blocks.FURNACE is null")
        }
    }

    @Test
    fun testFurnaceRemoval() {
        val pos = BlockPos.ORIGIN
        world.setBlockState(pos, Blocks.FURNACE.defaultState)
        world.setBlockToAir(pos)
        TestUtils.tickServer()
        assertTrue(world.getTileEntity(pos) == null)
    }

    @Test
    fun testFurnaceSmelt() {
        val pos = BlockPos.ORIGIN
        world.setBlockState(pos, Blocks.FURNACE.defaultState)
        val furnace = world.getTileEntity(pos) as TileEntityFurnace

        val coal = ItemStack(Items.COAL)
        val ore = ItemStack(Blocks.IRON_ORE)
        val ingot = ItemStack(Items.IRON_INGOT)

        val furnaceData = furnace.tileData
        var cookTime: Int

        // 0 = input, 1 = fuel, 2 = output
        furnace.setInventorySlotContents(0, ore)
        furnace.setInventorySlotContents(1, coal)
        for (i in 0..2399) {
            Barometer.server.tick()
            cookTime = furnaceData.getInteger("CookTime")
            if (cookTime > 0 && furnace.getStackInSlot(2) != null) {
                break
            }
        }
        assertTrue(ingot.isItemEqual(furnace.getStackInSlot(2)))
    }
}

package com.jjtparadox.barometer.test;

import com.jjtparadox.barometer.Barometer;
import com.jjtparadox.barometer.TestUtils;
import com.jjtparadox.barometer.tester.BarometerTester;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.*;

@RunWith(BarometerTester.class)
public class BarometerExampleTestJava {
    World world;

    @Before
    public void setUp() {
        world = Barometer.getServer().getEntityWorld();
    }

    @Test
    public void testChestPlacement() {
        if (Blocks.CHEST != null) {
            BlockPos pos = new BlockPos(0, 0, 0);
            world.setBlockState(pos, Blocks.CHEST.getDefaultState());
            IBlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            assertEquals(block, Blocks.CHEST);
            TileEntity tile = world.getTileEntity(pos);
            assertSame(tile.getClass(), TileEntityChest.class);
        } else {
            fail("Blocks.CHEST is null");
        }
    }

    @Test
    public void testChestRemoval() {
        BlockPos pos = new BlockPos(0, 0, 0);
        world.setBlockState(pos, Blocks.CHEST.getDefaultState());
        world.setBlockToAir(pos);
        TestUtils.tickServer();
        assertNull(world.getTileEntity(pos));
    }

    @Test
    public void testHopperFunctionality() {
        BlockPos upperChestPos = new BlockPos(0, 1, 0);
        BlockPos hopperPos = new BlockPos(0, 0, 0);

        world.setBlockState(upperChestPos, Blocks.CHEST.getDefaultState());
        TileEntityChest chest = (TileEntityChest) world.getTileEntity(upperChestPos);
        ItemStack item = new ItemStack(Blocks.STONE);
        chest.setInventorySlotContents(0, item.copy());

        world.setBlockState(hopperPos, Blocks.HOPPER.getDefaultState());
        TileEntityHopper hopper = (TileEntityHopper) world.getTileEntity(hopperPos);

        for (int i = 0; i < 200; i++) {
            TestUtils.tickServer();
            if (!hopper.getStackInSlot(0).isEmpty()) {
                break;
            }
        }

        assertTrue(item.isItemEqual(hopper.getStackInSlot(0)));
    }

}

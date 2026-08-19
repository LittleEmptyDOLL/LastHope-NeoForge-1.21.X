package com.github.littleemptydoll.lasthope.block.decoration;

import com.github.littleemptydoll.lasthope.block.container.AbstractContainerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TestStorageBlock extends AbstractContainerBlock {
    public TestStorageBlock(Properties properties) {
        super(properties);
    }

    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 16, 16);

    @Override
    protected VoxelShape getBlockShape() {
        return SHAPE;
    }
}

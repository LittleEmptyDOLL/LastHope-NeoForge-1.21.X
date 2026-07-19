package com.github.littleemptydoll.lasthope.block.decoration;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CardboardBoxBlock extends AbstractDecorativeBlock {
    public CardboardBoxBlock(Properties properties) {
        super(properties);
    }

    private static final VoxelShape SHAPE = Block.box(3, 0, 1, 13, 8, 15);

    @Override
    protected VoxelShape getBlockShape() {
        return SHAPE;
    }
}

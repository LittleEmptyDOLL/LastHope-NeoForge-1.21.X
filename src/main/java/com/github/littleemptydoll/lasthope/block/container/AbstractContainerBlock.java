package com.github.littleemptydoll.lasthope.block.container;

import com.github.littleemptydoll.lasthope.block.decoration.AbstractDecorativeBlock;
import com.github.littleemptydoll.lasthope.blockentity.GenericContainerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractContainerBlock
        extends AbstractDecorativeBlock
        implements EntityBlock {
    protected AbstractContainerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GenericContainerBlockEntity(pos, state);
    }
}

package com.github.littleemptydoll.lasthope.block.multiblock;

import com.github.littleemptydoll.lasthope.registry.definition.BlockDefinition;
import com.github.littleemptydoll.lasthope.registry.definition.BlockDefinitionRegistry;
import com.github.littleemptydoll.lasthope.registry.definition.MultiBlockDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public abstract class AbstractMultiBlockBlock extends com.github.littleemptydoll.lasthope.block.decoration.AbstractDecorativeBlock {
    public static final IntegerProperty PART = IntegerProperty.create("part", 0, 255);

    protected AbstractMultiBlockBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(PART, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PART);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state == null) {
            return null;
        }

        MultiBlockDefinition definition = multiBlockDefinition(state);
        int anchorPart = definition == null ? 0 : definition.anchorIndex();
        return state.setValue(PART, anchorPart);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        MultiBlockDefinition definition = multiBlockDefinition(state);
        if (definition == null) {
            return super.canSurvive(state, level, pos);
        }

        Direction facing = state.getValue(FACING);
        BlockPos anchor = anchorPos(pos, definition, state.getValue(PART), facing);

        for (int index : definition.occupiedParts()) {
            BlockPos target = anchor.offset(offset(definition, index, facing));
            if (target.equals(pos)) {
                continue;
            }

            BlockState targetState = level.getBlockState(target);
            if (!targetState.isAir() && !targetState.canBeReplaced()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void setPlacedBy(
            Level level,
            BlockPos pos,
            BlockState state,
            LivingEntity placer,
            ItemStack stack
    ) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (level.isClientSide) {
            return;
        }

        MultiBlockDefinition definition = multiBlockDefinition(state);
        if (definition == null || definition.parts() <= 1) {
            return;
        }

        Direction facing = state.getValue(FACING);
        BlockPos anchor = anchorPos(pos, definition, state.getValue(PART), facing);

        for (int index : definition.occupiedParts()) {
            if (index == definition.anchorIndex()) {
                continue;
            }

            level.setBlock(
                    anchor.offset(offset(definition, index, facing)),
                    state.setValue(PART, index),
                    Block.UPDATE_ALL
            );
        }
    }

    @Override
    public BlockState playerWillDestroy(
            Level level,
            BlockPos pos,
            BlockState state,
            net.minecraft.world.entity.player.Player player
    ) {
        if (!level.isClientSide) {
            removeOtherParts(level, pos, state);
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    private void removeOtherParts(Level level, BlockPos pos, BlockState state) {
        MultiBlockDefinition definition = multiBlockDefinition(state);
        if (definition == null || definition.parts() <= 1) {
            return;
        }

        int part = state.getValue(PART);
        Direction facing = state.getValue(FACING);
        BlockPos anchor = anchorPos(pos, definition, part, facing);

        for (int index : definition.occupiedParts()) {
            if (index == part) {
                continue;
            }

            BlockPos target = anchor.offset(offset(definition, index, facing));
            BlockState targetState = level.getBlockState(target);
            if (targetState.is(this)
                    && targetState.hasProperty(PART)
                    && targetState.getValue(PART) == index
                    && targetState.getValue(FACING) == facing) {
                level.setBlock(target, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    private BlockPos anchorPos(
            BlockPos pos,
            MultiBlockDefinition definition,
            int part,
            Direction facing
    ) {
        return pos.subtract(offset(definition, part, facing));
    }

    private BlockPos offset(
            MultiBlockDefinition definition,
            int index,
            Direction facing
    ) {
        int x = definition.relativeX(index);
        int y = definition.relativeY(index);
        int z = definition.relativeZ(index);

        return switch (facing) {
            case EAST -> new BlockPos(-z, y, x);
            case SOUTH -> new BlockPos(-x, y, -z);
            case WEST -> new BlockPos(z, y, -x);
            default -> new BlockPos(x, y, z);
        };
    }

    private MultiBlockDefinition multiBlockDefinition(BlockState state) {
        BlockDefinition definition = BlockDefinitionRegistry.get(state);
        return definition == null ? null : definition.multiBlock();
    }
}

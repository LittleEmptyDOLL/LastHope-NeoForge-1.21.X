package com.github.littleemptydoll.lasthope.block.decoration;

import com.github.littleemptydoll.lasthope.block.BlockShape;
import com.github.littleemptydoll.lasthope.block.BlockRotation;
import com.github.littleemptydoll.lasthope.registry.definition.BlockDefinition;
import com.github.littleemptydoll.lasthope.registry.definition.BlockDefinitionRegistry;
import com.github.littleemptydoll.lasthope.registry.definition.BlockPlacement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public abstract class AbstractDecorativeBlock extends Block {
    public static final net.minecraft.world.level.block.state.properties.DirectionProperty FACING =
            BlockStateProperties.HORIZONTAL_FACING;

    public AbstractDecorativeBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    /**
     * Legacy per-block shape hook. Definitions may provide a BlockShape instead.
     */
    protected VoxelShape getBlockShape() {
        return Shapes.block();
    }

    @Override
    protected VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        BlockDefinition definition = BlockDefinitionRegistry.get(state);
        if (definition != null) {
            BlockShape shape = definition.shape();
            if (shape != null) {
                return shape.get(state, definition.placement().rotation());
            }
        }

        return getBlockShape();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockDefinition definition = BlockDefinitionRegistry.get(this);
        BlockPlacement placement = definition == null
                ? BlockPlacement.none()
                : definition.placement();

        Direction facing = placement.rotation() == BlockRotation.HORIZONTAL
                ? context.getHorizontalDirection().getOpposite()
                : Direction.NORTH;

        return defaultBlockState().setValue(FACING, facing);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        if (!state.hasProperty(FACING)) {
            return state;
        }

        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        if (!state.hasProperty(FACING)) {
            return state;
        }

        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }
}

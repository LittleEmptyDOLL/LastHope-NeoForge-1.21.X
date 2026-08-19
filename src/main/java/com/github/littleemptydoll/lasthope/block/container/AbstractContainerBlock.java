package com.github.littleemptydoll.lasthope.block.container;

import com.github.littleemptydoll.lasthope.block.decoration.AbstractDecorativeBlock;
import com.github.littleemptydoll.lasthope.blockentity.GenericContainerBlockEntity;
import com.github.littleemptydoll.lasthope.menu.GenericContainerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public abstract class AbstractContainerBlock
        extends AbstractDecorativeBlock
        implements EntityBlock {
    protected AbstractContainerBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hit
    ) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.PASS;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (!(blockEntity instanceof GenericContainerBlockEntity container)) {
            return InteractionResult.PASS;
        }

        serverPlayer.openMenu(
                new SimpleMenuProvider(
                        (containerId, inventory, ignorePlayer) ->
                                new GenericContainerMenu(
                                        containerId,
                                        inventory,
                                        container
                                ),
                        Component.translatable(
                                state.getBlock().getDescriptionId()
                        )
                ),
                buffer -> buffer.writeBlockPos(pos)
        );

        return InteractionResult.CONSUME;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GenericContainerBlockEntity(pos, state);
    }
}

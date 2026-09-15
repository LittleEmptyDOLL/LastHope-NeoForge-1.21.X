package com.github.littleemptydoll.lasthope.block.container;

import com.github.littleemptydoll.lasthope.block.decoration.AbstractDecorativeBlock;
import com.github.littleemptydoll.lasthope.blockentity.GenericContainerBlockEntity;
import com.github.littleemptydoll.lasthope.menu.GenericContainerMenu;
import com.github.littleemptydoll.lasthope.registry.definition.BlockDefinition;
import com.github.littleemptydoll.lasthope.registry.definition.BlockDefinitionRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;

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

        container.startOpen(player);

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

        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity instanceof GenericContainerBlockEntity container) {
            container.loadFromItemStack(stack);
        }
    }

    @Override
    protected List<ItemStack> getDrops(
            BlockState state,
            LootParams.Builder params
    ) {
        List<ItemStack> drops = super.getDrops(state, params);

        BlockDefinition definition = BlockDefinitionRegistry.get(state);

        if (definition == null || definition.containerSettings() == null) {
            return drops;
        }

        BlockEntity blockEntity = params.getOptionalParameter(
                LootContextParams.BLOCK_ENTITY
        );

        if (!(blockEntity instanceof GenericContainerBlockEntity container)) {
            return drops;
        }

        if (definition.containerSettings().preserveInventory()) {
            ItemStack containerItem = drops.stream()
                    .filter(stack -> stack.is(asItem()))
                    .findFirst()
                    .orElse(null);

            if (containerItem != null && !container.isEmpty()) {
                container.saveToItemStack(containerItem);
            }
        } else {
            for (ItemStack stack : container.getItems()) {
                if (!stack.isEmpty()) {
                    drops.add(stack.copy());
                }
            }
        }

        return drops;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GenericContainerBlockEntity(pos, state);
    }
}

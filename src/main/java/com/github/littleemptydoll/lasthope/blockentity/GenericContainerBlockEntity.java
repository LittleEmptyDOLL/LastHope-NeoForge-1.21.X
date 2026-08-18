package com.github.littleemptydoll.lasthope.blockentity;

import com.github.littleemptydoll.lasthope.registry.BlockEntityRegistry;
import com.github.littleemptydoll.lasthope.registry.definition.BlockDefinition;
import com.github.littleemptydoll.lasthope.registry.definition.BlockDefinitionRegistry;
import com.github.littleemptydoll.lasthope.registry.definition.settings.InventoryLayout;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class GenericContainerBlockEntity extends BlockEntity implements Container {
    public GenericContainerBlockEntity(
            BlockPos pos,
            BlockState state
    ) {
        super(
                BlockEntityRegistry.GENERIC_CONTAINER.get(),
                pos,
                state
        );

        BlockDefinition definition = BlockDefinitionRegistry.get(state);

        if (definition == null) {
            throw new IllegalStateException(
                    "No BlockDefinition found for block: " +
                    state.getBlock()
            );
        }
        if (definition.containerSettings() == null) {
            throw new IllegalStateException(
                    "Block has no ContainerSettings: " +
                    state.getBlock()
            );
        }

        int size = definition.containerSettings().layout().slots();

        this.items = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    @Override
    protected void saveAdditional(
            CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        super.saveAdditional(tag, registries);
        // Сохранение предметов
        ContainerHelper.saveAllItems(tag, items, registries);
    }

    @Override
    protected void loadAdditional(
            CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        super.loadAdditional(tag, registries);
        // Загрузка предметов
        ContainerHelper.loadAllItems(tag, items, registries);
    }

    private NonNullList<ItemStack> items;

    private static final String ITEMS_TAG = "Items";

    public NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack item : items) {
            if (!item.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = ContainerHelper.removeItem(items, slot, amount);

        if (!result.isEmpty()) {
            setChanged();
        }

        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);

        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }

        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        if (level == null || level.getBlockEntity(worldPosition) != this) {
            return false;
        }

        return player.distanceToSqr(
                worldPosition.getX() + 0.5,
                worldPosition.getY() + 0.5,
                worldPosition.getZ() + 0.5
        ) <= 64.0;
    }

    @Override
    public void clearContent() {
        items.clear();
        setChanged();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return true;
    }

    public InventoryLayout getInventoryLayout() {
        return BlockDefinitionRegistry.get(getBlockState())
                .containerSettings()
                .layout();
    }
}

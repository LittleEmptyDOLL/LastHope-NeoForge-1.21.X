package com.github.littleemptydoll.lasthope.menu;

import com.github.littleemptydoll.lasthope.blockentity.GenericContainerBlockEntity;
import com.github.littleemptydoll.lasthope.client.screen.ContainerGuiConstants;
import com.github.littleemptydoll.lasthope.registry.MenuRegistry;
import com.github.littleemptydoll.lasthope.registry.definition.settings.InventoryLayout;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class GenericContainerMenu extends AbstractContainerMenu {
    private final BlockPos blockPos;
    private final GenericContainerBlockEntity blockEntity;
    private final int containerSlotCount;

    //Клиентский конструктор
    public GenericContainerMenu(
            int containerId,
            Inventory playerInventory,
            FriendlyByteBuf buffer
    ) {
        this(
                containerId,
                playerInventory,
                buffer.readBlockPos()
        );
    }

    //Основной конструктор
    public GenericContainerMenu(
            int containerId,
            Inventory playerInventory,
            BlockPos blockPos
    ) {
        super(
                MenuRegistry.GENERIC_CONTAINER.get(),
                containerId
        );

        this.blockPos = blockPos;

        if (playerInventory.player.level().getBlockEntity(blockPos)
                instanceof GenericContainerBlockEntity container) {
            this.blockEntity = container;
        } else {
            throw new IllegalStateException(
                    "No GenericContainerBlockEntity found at " + blockPos
            );
        }

        this.containerSlotCount = blockEntity.getContainerSize();

        InventoryLayout layout = blockEntity.getInventoryLayout();

        int columns = layout.columns();

        //Контейнер
        int containerWidth = layout.columns() * ContainerGuiConstants.SLOT_SIZE;

        int playerInventoryWidth = ContainerGuiConstants.PLAYER_INVENTORY_COLUMNS + ContainerGuiConstants.SLOT_SIZE;

        int containerX = ContainerGuiConstants.PADDING + (playerInventoryWidth - containerWidth) / 2;

        for (int slot = 0; slot < containerSlotCount; slot++) {
            int row = slot / columns;
            int column = slot % columns;

            addSlot(new Slot(
                    blockEntity,
                    slot,
                    containerX + column * ContainerGuiConstants.SLOT_SIZE,
                    ContainerGuiConstants.SLOT_SIZE + row * ContainerGuiConstants.SLOT_SIZE
            ));
        }
        //Основной инвентарь игрока
        int playerInventoryY =
                ContainerGuiConstants.SLOT_SIZE
                        + layout.rows() * ContainerGuiConstants.SLOT_SIZE
                        + ContainerGuiConstants.SECTION_GAP;

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(
                        playerInventory,
                        column
                                + row * ContainerGuiConstants.PLAYER_INVENTORY_COLUMNS
                                + ContainerGuiConstants.PLAYER_INVENTORY_COLUMNS,
                        ContainerGuiConstants.PADDING + column * ContainerGuiConstants.SLOT_SIZE,
                        playerInventoryY + row * ContainerGuiConstants.SLOT_SIZE
                ));
            }
        }
        //Хотбар
        int hotbarY = playerInventoryY + 3 * ContainerGuiConstants.SLOT_SIZE + ContainerGuiConstants.SECTION_GAP;

        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(
                    playerInventory,
                    column,
                    ContainerGuiConstants.PADDING
                            + column * ContainerGuiConstants.SLOT_SIZE,
                    hotbarY
            ));
        }
    }

    //Позиция блока с которым работает меню
    public BlockPos getBlockPos() {
        return blockPos;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        Slot slot = slots.get(slotIndex);

        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack sourceStack = slot.getItem();
        ItemStack resultStack = sourceStack.copy();

        if (slotIndex < containerSlotCount) {
            //Контейнер -> инвентарь игрока
            if (!moveItemStackTo(
                    sourceStack,
                    containerSlotCount,
                    slots.size(),
                    true
            )) {
                return ItemStack.EMPTY;
            }
        } else {
            //Инвентарь игрока -> контейнер
            if (!moveItemStackTo(
                    sourceStack,
                    0,
                    containerSlotCount,
                    false
            )) {
                return ItemStack.EMPTY;
            }
        }

        if (sourceStack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return resultStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return blockEntity.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
//        blockEntity.stopOpen(player);
    }

    public InventoryLayout getInventoryLayout() {
        return blockEntity.getInventoryLayout();
    }

    public int getContainerSlotCount() {
        return containerSlotCount;
    }
}

package com.github.littleemptydoll.lasthope.menu;

import com.github.littleemptydoll.lasthope.blockentity.GenericContainerBlockEntity;
import com.github.littleemptydoll.lasthope.container.ContainerGuiConstants;
import com.github.littleemptydoll.lasthope.container.ContainerGuiGeometry;
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

    private final GenericContainerBlockEntity blockEntity;
    private final InventoryLayout inventoryLayout;

    //Серверный конструктор
    public GenericContainerMenu(
            int containerId,
            Inventory playerInventory,
            GenericContainerBlockEntity blockEntity
    ) {
        super(MenuRegistry.GENERIC_CONTAINER.get(), containerId);

        this.blockEntity = blockEntity;
        this.inventoryLayout = blockEntity.getInventoryLayout();

        ContainerGuiGeometry geometry = new ContainerGuiGeometry(inventoryLayout);

        addContainerSlots(geometry);
        addPlayerInventorySlots(geometry, playerInventory);
    }

    //Клиентский конструктор
    public GenericContainerMenu(
            int containerId,
            Inventory playerInventory,
            FriendlyByteBuf buffer
    ) {
        this(
                containerId,
                playerInventory,
                getBlockEntity(playerInventory, buffer)
        );
    }

    //Контейнер
    private void addContainerSlots(
            ContainerGuiGeometry geometry
    ) {
        int columns = inventoryLayout.columns();
        int slots = inventoryLayout.slots();

        int startX = geometry.containerX() + ContainerGuiConstants.PADDING;
        int startY = geometry.containerSlotY();

        for (int index = 0; index < slots; index++) {
            int row = index / columns;
            int column = index % columns;

            int x = startX + column * ContainerGuiConstants.SLOT_SIZE;
            int y = startY + row * ContainerGuiConstants.SLOT_SIZE;

            addSlot(
                    new Slot(
                            blockEntity,
                            index,
                            x,
                            y
                    )
            );
        }
    }

    private void addPlayerInventorySlots(
            ContainerGuiGeometry geometry,
            Inventory playerInventory
    ) {
        int startX = geometry.playerInventoryX() + ContainerGuiConstants.PADDING;

        int inventoryY = geometry.playerInventorySlotY();

        //Основной инвентарь игрока
        for (int row = 0; row < ContainerGuiConstants.PLAYER_INVENTORY_ROWS; row++) {
            for (int column = 0; column < ContainerGuiConstants.PLAYER_INVENTORY_COLUMNS; column++) {
                int index = column
                        + row * ContainerGuiConstants.PLAYER_INVENTORY_COLUMNS
                        + ContainerGuiConstants.PLAYER_INVENTORY_COLUMNS;

                int x = startX + column * ContainerGuiConstants.SLOT_SIZE;
                int y = inventoryY + row * ContainerGuiConstants.SLOT_SIZE;

                addSlot(
                        new Slot(
                                playerInventory,
                                index,
                                x,
                                y
                        )
                );
            }
        }
        //Хотбар
        int hotbarY = geometry.hotbarY();

        for (int column = 0; column < ContainerGuiConstants.PLAYER_INVENTORY_COLUMNS; column++) {
            int x = startX + column * ContainerGuiConstants.SLOT_SIZE;

            addSlot(
                    new Slot(
                            playerInventory,
                            column,
                            x,
                            hotbarY
                    )
            );
        }
    }

    public InventoryLayout getInventoryLayout() {
        return inventoryLayout;
    }

    private static GenericContainerBlockEntity getBlockEntity(
            Inventory playerInventory,
            FriendlyByteBuf buffer
    ) {
        BlockPos pos = buffer.readBlockPos();

        if (!(playerInventory.player.level().getBlockEntity(pos)
                instanceof GenericContainerBlockEntity blockEntity)) {
            throw new IllegalStateException(
                    "Expected GenericContainerBlockEntity at " + pos
            );
        }

        return blockEntity;
    }

    @Override
    public ItemStack quickMoveStack(
            Player player,
            int index
    ) {
        Slot slot = slots.get(index);

        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();

        int containerSlots = inventoryLayout.slots();
        int playerInventoryStart = containerSlots;
        int playerInventoryEnd = slots.size();

        if (index < containerSlots) {
            //Контейнер -> инвентарь игрока
            if (!moveItemStackTo(
                    stack,
                    playerInventoryStart,
                    playerInventoryEnd,
                    true
            )) {
                return ItemStack.EMPTY;
            }
        } else {
            //Инвентарь игрока -> контейнер
            if (!moveItemStackTo(
                    stack,
                    0,
                    containerSlots,
                    false
            )) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return copy;
    }

    @Override
    public boolean stillValid(Player player) {
        return blockEntity.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);

        blockEntity.stopOpen(player);
    }
}

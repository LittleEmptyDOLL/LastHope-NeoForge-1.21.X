package com.github.littleemptydoll.lasthope.container;

import com.github.littleemptydoll.lasthope.registry.definition.settings.InventoryLayout;

public final class ContainerGuiGeometry {
    private final InventoryLayout layout;

    private final int containerWidth;
    private final int containerHeight;

    private final int playerInventoryWidth;
    private final int playerInventoryHeight;

    private final int imageWidth;
    private final int imageHeight;

    public ContainerGuiGeometry(InventoryLayout layout) {
        this.layout = layout;

        containerWidth =
                layout.columns() * ContainerGuiConstants.SLOT_SIZE
                        + ContainerGuiConstants.PADDING * 2;

        containerHeight =
                ContainerGuiConstants.TITLE_HEIGHT
                        + layout.rows() * ContainerGuiConstants.SLOT_SIZE
                        + ContainerGuiConstants.PADDING;

        playerInventoryWidth =
                ContainerGuiConstants.PLAYER_INVENTORY_WIDTH
                        + ContainerGuiConstants.PADDING * 2;

        playerInventoryHeight =
                ContainerGuiConstants.TITLE_HEIGHT
                        + ContainerGuiConstants.PLAYER_INVENTORY_ROWS
                        * ContainerGuiConstants.SLOT_SIZE
                        + ContainerGuiConstants.SECTION_GAP
                        + ContainerGuiConstants.HOTBAR_ROWS
                        * ContainerGuiConstants.SLOT_SIZE
                        + ContainerGuiConstants.PADDING;

        imageWidth = Math.max(
                containerWidth,
                playerInventoryWidth
        );
        imageHeight =
                containerHeight
                        + ContainerGuiConstants.SECTION_GAP
                        + playerInventoryHeight;
    }

    public int containerX() {
        return (imageWidth - containerWidth) / 2;
    }

    public int containerY() {
        return 0;
    }

    public int containerSlotY() {
        return containerY() + ContainerGuiConstants.TITLE_HEIGHT;
    }

    public int playerInventoryX() {
        return (imageWidth - playerInventoryWidth) / 2;
    }

    public int playerInventoryY() {
        return containerHeight + ContainerGuiConstants.SECTION_GAP;
    }

    public int containerWidth() {
        return containerWidth;
    }

    public int containerHeight() {
        return containerHeight;
    }

    public int playerInventoryWidth() {
        return playerInventoryWidth;
    }

    public int playerInventoryHeight() {
        return playerInventoryHeight;
    }

    public int playerInventorySlotY() {
        return playerInventoryY() + ContainerGuiConstants.TITLE_HEIGHT;
    }

    public int hotbarY() {
        return playerInventorySlotY()
                + ContainerGuiConstants.PLAYER_INVENTORY_ROWS
                * ContainerGuiConstants.SLOT_SIZE
                + ContainerGuiConstants.PLAYER_INVENTORY_HOTBAR_GAP;
    }

    public int imageWidth() {
        return imageWidth;
    }

    public int imageHeight() {
        return imageHeight;
    }
}

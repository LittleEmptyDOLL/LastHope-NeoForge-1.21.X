package com.github.littleemptydoll.lasthope.container;

public final class ContainerGuiConstants {
    private ContainerGuiConstants() {}

    public static final int SLOT_SIZE = 18;
    public static final int PADDING = 8;

    public static final int TITLE_HEIGHT = 18;
    public static final int SECTION_GAP = 12;

    public static final int PLAYER_INVENTORY_ROWS = 3;
    public static final int PLAYER_INVENTORY_COLUMNS = 9;

    public static final int HOTBAR_ROWS = 1;

    public static final int PLAYER_INVENTORY_WIDTH = PLAYER_INVENTORY_COLUMNS * SLOT_SIZE;

    public static final int PLAYER_INVENTORY_HEIGHT =
            PLAYER_INVENTORY_ROWS * SLOT_SIZE
                    + SECTION_GAP
                    + HOTBAR_ROWS * SLOT_SIZE;
}

package com.github.littleemptydoll.lasthope.registry.definition.settings;

public final class InventoryLayouts {
    private InventoryLayouts() {}

    public static final InventoryLayout SMALL_BOX = InventoryLayout.of(3, 3);

    public static final InventoryLayout BOX = InventoryLayout.of(1, 9);

    public static final InventoryLayout CHEST = InventoryLayout.of(3, 9);

    public static final InventoryLayout DOUBLE_CHEST = InventoryLayout.of(6, 9);

    public static final InventoryLayout MEDKIT = InventoryLayout.of(2, 3);

    public static final InventoryLayout CASE = InventoryLayout.of(2, 4);

    public static final InventoryLayout CAR_TRUNK = InventoryLayout.of(4, 9);

    public static final InventoryLayout SOME_BIG_BOX = InventoryLayout.of(10, 12);
}

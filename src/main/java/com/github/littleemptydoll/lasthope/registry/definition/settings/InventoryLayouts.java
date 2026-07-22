package com.github.littleemptydoll.lasthope.registry.definition.settings;

public final class InventoryLayouts {
    private InventoryLayouts() {}

    public static final InventoryLayout BOX = new InventoryLayout(1, 9);

    public static final InventoryLayout CHEST = new InventoryLayout(3, 9);

    public static final InventoryLayout DOUBLE_CHEST = new InventoryLayout(6, 9);

    public static final InventoryLayout MEDKIT = new InventoryLayout(3, 3);

    public static final InventoryLayout CASE = new InventoryLayout(2, 4);
}

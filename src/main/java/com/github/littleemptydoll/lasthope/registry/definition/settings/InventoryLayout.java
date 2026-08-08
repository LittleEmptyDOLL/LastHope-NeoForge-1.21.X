package com.github.littleemptydoll.lasthope.registry.definition.settings;

public final class InventoryLayout {
    private final int rows;
    private final int columns;

    private InventoryLayout(int rows, int columns) {
        if (rows <= 0 || columns <= 0) {
            throw new IllegalArgumentException("Inventory size must be positive.");
        }

        this.rows = rows;
        this.columns = columns;
    }

    public static InventoryLayout of(int rows, int columns) {
        return new InventoryLayout(rows, columns);
    }

    public int rows() {
        return rows;
    }

    public int columns() {
        return columns;
    }

    public int slots() {
        return rows * columns;
    }
}

package com.github.littleemptydoll.lasthope.registry.definition.settings;

public record InventoryLayout(
        int rows,
        int columns
) {
    public int slots() {
        return rows * columns;
    }
}

package com.github.littleemptydoll.lasthope.registry.definition;

public final class MultiBlockDefinition {
    private final int width;
    private final int height;
    private final int depth;

    private MultiBlockDefinition(int width, int height, int depth) {
        if (width < 1 || height < 1 || depth < 1) {
            throw new IllegalArgumentException("Multiblock dimensions must be positive");
        }

        long parts = (long) width * height * depth;
        if (parts > 256) {
            throw new IllegalArgumentException("Multiblock cannot contain more than 256 parts");
        }

        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    public static MultiBlockDefinition of(int width, int height, int depth) {
        return new MultiBlockDefinition(width, height, depth);
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public int depth() {
        return depth;
    }

    public int parts() {
        return width * height * depth;
    }

    public int index(int x, int y, int z) {
        return x + width * (z + depth * y);
    }

    public int x(int index) {
        return index % width;
    }

    public int y(int index) {
        return index / (width * depth);
    }

    public int z(int index) {
        return (index / width) % depth;
    }
}

package com.github.littleemptydoll.lasthope.registry.definition;

import java.util.ArrayList;
import java.util.List;

public final class MultiBlockDefinition {
    private final int width;
    private final int height;
    private final int depth;
    private final int anchorX;
    private final int anchorY;
    private final int anchorZ;
    private final List<String> partModels;

    private MultiBlockDefinition(
            int width,
            int height,
            int depth,
            int anchorX,
            int anchorY,
            int anchorZ,
            List<String> partModels
    ) {
        if (width < 1 || height < 1 || depth < 1) {
            throw new IllegalArgumentException("Multiblock dimensions must be positive");
        }

        long parts = (long) width * height * depth;
        if (parts > 256) {
            throw new IllegalArgumentException("Multiblock cannot contain more than 256 parts");
        }

        if (anchorX < 0 || anchorX >= width
                || anchorY < 0 || anchorY >= height
                || anchorZ < 0 || anchorZ >= depth) {
            throw new IllegalArgumentException("Multiblock anchor must be inside the definition bounds");
        }

        if (partModels.size() != parts) {
            throw new IllegalArgumentException("Multiblock part model list must contain exactly " + parts + " entries");
        }

        this.width = width;
        this.height = height;
        this.depth = depth;
        this.anchorX = anchorX;
        this.anchorY = anchorY;
        this.anchorZ = anchorZ;
        this.partModels = List.copyOf(partModels);
    }

    public static MultiBlockDefinition of(int width, int height, int depth) {
        return of(width, height, depth, 0, 0, 0);
    }

    public static MultiBlockDefinition of(
            int width,
            int height,
            int depth,
            int anchorX,
            int anchorY,
            int anchorZ
    ) {
        int parts = Math.multiplyExact(Math.multiplyExact(width, height), depth);
        return new MultiBlockDefinition(
                width,
                height,
                depth,
                anchorX,
                anchorY,
                anchorZ,
                new ArrayList<>(java.util.Collections.nCopies(parts, null))
        );
    }

    public MultiBlockDefinition partModel(int index, String modelSuffix) {
        if (index < 0 || index >= parts()) {
            throw new IllegalArgumentException("Invalid multiblock part index: " + index);
        }
        if (modelSuffix == null || modelSuffix.isBlank()) {
            throw new IllegalArgumentException("Multiblock part model suffix must not be blank");
        }

        List<String> models = new ArrayList<>(partModels);
        models.set(index, modelSuffix);
        return new MultiBlockDefinition(
                width,
                height,
                depth,
                anchorX,
                anchorY,
                anchorZ,
                models
        );
    }

    public String partModel(int index) {
        if (index < 0 || index >= parts()) {
            throw new IllegalArgumentException("Invalid multiblock part index: " + index);
        }
        return partModels.get(index);
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

    public int anchorX() {
        return anchorX;
    }

    public int anchorY() {
        return anchorY;
    }

    public int anchorZ() {
        return anchorZ;
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

    public int anchorIndex() {
        return index(anchorX, anchorY, anchorZ);
    }

    public int relativeX(int index) {
        return x(index) - anchorX;
    }

    public int relativeY(int index) {
        return y(index) - anchorY;
    }

    public int relativeZ(int index) {
        return z(index) - anchorZ;
    }
}

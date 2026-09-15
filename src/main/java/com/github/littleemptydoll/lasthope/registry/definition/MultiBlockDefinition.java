package com.github.littleemptydoll.lasthope.registry.definition;

import com.github.littleemptydoll.lasthope.block.BlockShape;
import net.minecraft.core.BlockPos;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class MultiBlockDefinition {
    private final int width;
    private final int height;
    private final int depth;
    private final int anchorX;
    private final int anchorY;
    private final int anchorZ;
    private final Set<Integer> occupiedParts;
    private final BlockShape collision;

    private MultiBlockDefinition(
            int width,
            int height,
            int depth,
            int anchorX,
            int anchorY,
            int anchorZ,
            Set<Integer> occupiedParts,
            BlockShape collision
    ) {
        if (width < 1 || height < 1 || depth < 1) {
            throw new IllegalArgumentException("Multiblock dimensions must be positive");
        }

        long cells = (long) width * height * depth;
        if (cells > 256) {
            throw new IllegalArgumentException("Multiblock cannot contain more than 256 cells");
        }

        if (anchorX < 0 || anchorX >= width
                || anchorY < 0 || anchorY >= height
                || anchorZ < 0 || anchorZ >= depth) {
            throw new IllegalArgumentException("Multiblock anchor must be inside the definition bounds");
        }

        int anchorIndex = index(width, depth, anchorX, anchorY, anchorZ);
        if (!occupiedParts.contains(anchorIndex)) {
            throw new IllegalArgumentException("Multiblock anchor must be an occupied cell");
        }

        this.width = width;
        this.height = height;
        this.depth = depth;
        this.anchorX = anchorX;
        this.anchorY = anchorY;
        this.anchorZ = anchorZ;
        this.occupiedParts = Set.copyOf(occupiedParts);
        this.collision = collision;
    }

    public static MultiBlockDefinition of(BlockShape collision, int anchorX, int anchorY, int anchorZ) {
        List<BlockPos> occupied = collision.occupiedCells();
        if (occupied.isEmpty()) {
            throw new IllegalArgumentException("Multiblock collision must occupy at least one cell");
        }

        int width = occupied.stream().mapToInt(BlockPos::getX).max().orElseThrow() + 1;
        int height = occupied.stream().mapToInt(BlockPos::getY).max().orElseThrow() + 1;
        int depth = occupied.stream().mapToInt(BlockPos::getZ).max().orElseThrow() + 1;

        Set<Integer> parts = new HashSet<>();
        for (BlockPos pos : occupied) {
            parts.add(index(width, depth, pos.getX(), pos.getY(), pos.getZ()));
        }

        return new MultiBlockDefinition(
                width,
                height,
                depth,
                anchorX,
                anchorY,
                anchorZ,
                parts,
                collision
        );
    }

    public static MultiBlockDefinition of(BlockShape collision) {
        return of(collision, 0, 0, 0);
    }

    public static MultiBlockDefinition of(
            int width,
            int height,
            int depth,
            int anchorX,
            int anchorY,
            int anchorZ
    ) {
        BlockShape collision = BlockShape.modelBox(
                0,
                0,
                0,
                width * 16.0,
                height * 16.0,
                depth * 16.0
        );

        return of(collision, anchorX, anchorY, anchorZ);
    }

    private static int index(int width, int depth, int x, int y, int z) {
        return x + width * (z + depth * y);
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

    public int cells() {
        return width * height * depth;
    }

    public int parts() {
        return occupiedParts.size();
    }

    public int index(int x, int y, int z) {
        return index(width, depth, x, y, z);
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

    public boolean isOccupied(int index) {
        return occupiedParts.contains(index);
    }

    public boolean isOccupied(int x, int y, int z) {
        return isOccupied(index(x, y, z));
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

    public Set<Integer> occupiedParts() {
        return occupiedParts;
    }

    public BlockShape collision() {
        return collision;
    }

    public BlockShape partShape(int index) {
        if (!isOccupied(index)) {
            throw new IllegalArgumentException("Invalid multiblock part index: " + index);
        }

        return collision.forCell(x(index), y(index), z(index));
    }
}

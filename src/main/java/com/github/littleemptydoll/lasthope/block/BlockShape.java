package com.github.littleemptydoll.lasthope.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class BlockShape {
    private final List<Box> boxes;

    private BlockShape(List<Box> boxes) {
        this.boxes = List.copyOf(boxes);
    }

    public static BlockShape cube() {
        return box(0, 0, 0, 16, 16, 16);
    }

    public static BlockShape box(
            double minX, double minY, double minZ,
            double maxX, double maxY, double maxZ
    ) {
        validateLocal(minX, minY, minZ, maxX, maxY, maxZ);
        return new BlockShape(List.of(
                new Box(minX, minY, minZ, maxX, maxY, maxZ)
        ));
    }

    public static BlockShape modelBox(
            double minX, double minY, double minZ,
            double maxX, double maxY, double maxZ
    ) {
        validateModel(minX, minY, minZ, maxX, maxY, maxZ);
        return new BlockShape(List.of(
                new Box(minX, minY, minZ, maxX, maxY, maxZ)
        ));
    }

    public BlockShape add(BlockShape shape) {
        List<Box> result = new ArrayList<>(boxes);
        result.addAll(shape.boxes);
        return new BlockShape(result);
    }

    public List<BlockPos> occupiedCells() {
        Set<BlockPos> occupied = new HashSet<>();

        for (Box box : boxes) {
            int minX = floorCell(box.minX);
            int minY = floorCell(box.minY);
            int minZ = floorCell(box.minZ);
            int maxX = ceilCell(box.maxX);
            int maxY = ceilCell(box.maxY);
            int maxZ = ceilCell(box.maxZ);

            for (int x = minX; x < maxX; x++) {
                for (int y = minY; y < maxY; y++) {
                    for (int z = minZ; z < maxZ; z++) {
                        if (box.intersectsCell(x, y, z)) {
                            occupied.add(new BlockPos(x, y, z));
                        }
                    }
                }
            }
        }

        return List.copyOf(occupied);
    }

    public BlockShape forCell(int cellX, int cellY, int cellZ) {
        List<Box> localBoxes = new ArrayList<>();
        double originX = cellX * 16.0;
        double originY = cellY * 16.0;
        double originZ = cellZ * 16.0;

        for (Box box : boxes) {
            double minX = Math.max(box.minX, originX);
            double minY = Math.max(box.minY, originY);
            double minZ = Math.max(box.minZ, originZ);
            double maxX = Math.min(box.maxX, originX + 16.0);
            double maxY = Math.min(box.maxY, originY + 16.0);
            double maxZ = Math.min(box.maxZ, originZ + 16.0);

            if (minX < maxX && minY < maxY && minZ < maxZ) {
                localBoxes.add(new Box(
                        minX - originX,
                        minY - originY,
                        minZ - originZ,
                        maxX - originX,
                        maxY - originY,
                        maxZ - originZ
                ));
            }
        }

        return new BlockShape(localBoxes);
    }

    public VoxelShape get(BlockState state, BlockRotation rotation) {
        Direction facing = state.getOptionalValue(BlockStateProperties.HORIZONTAL_FACING)
                .orElse(Direction.NORTH);

        int quarterTurns = rotation == BlockRotation.HORIZONTAL
                ? switch (facing) {
                    case EAST -> 1;
                    case SOUTH -> 2;
                    case WEST -> 3;
                    default -> 0;
                }
                : 0;

        VoxelShape result = Shapes.empty();
        for (Box box : boxes) {
            Box rotated = box.rotateY(quarterTurns);
            result = Shapes.or(result, Block.box(
                    rotated.minX,
                    rotated.minY,
                    rotated.minZ,
                    rotated.maxX,
                    rotated.maxY,
                    rotated.maxZ
            ));
        }

        return result.optimize();
    }

    private static int floorCell(double coordinate) {
        return (int) Math.floor(coordinate / 16.0);
    }

    private static int ceilCell(double coordinate) {
        return (int) Math.ceil(coordinate / 16.0);
    }

    private static void validateLocal(
            double minX, double minY, double minZ,
            double maxX, double maxY, double maxZ
    ) {
        if (minX < 0 || minY < 0 || minZ < 0
                || maxX > 16 || maxY > 16 || maxZ > 16
                || minX > maxX || minY > maxY || minZ > maxZ) {
            throw new IllegalArgumentException("Block shape must be inside 0..16");
        }
    }

    private static void validateModel(
            double minX, double minY, double minZ,
            double maxX, double maxY, double maxZ
    ) {
        if (minX < 0 || minY < 0 || minZ < 0
                || minX > maxX || minY > maxY || minZ > maxZ) {
            throw new IllegalArgumentException("Multiblock model shape must use non-negative coordinates");
        }
    }

    private record Box(
            double minX,
            double minY,
            double minZ,
            double maxX,
            double maxY,
            double maxZ
    ) {
        private boolean intersectsCell(int cellX, int cellY, int cellZ) {
            double minCellX = cellX * 16.0;
            double minCellY = cellY * 16.0;
            double minCellZ = cellZ * 16.0;
            double maxCellX = minCellX + 16.0;
            double maxCellY = minCellY + 16.0;
            double maxCellZ = minCellZ + 16.0;

            return minX < maxCellX && maxX > minCellX
                    && minY < maxCellY && maxY > minCellY
                    && minZ < maxCellZ && maxZ > minCellZ;
        }

        private Box rotateY(int quarterTurns) {
            double minX = this.minX;
            double minZ = this.minZ;
            double maxX = this.maxX;
            double maxZ = this.maxZ;

            for (int i = 0; i < quarterTurns; i++) {
                double nextMinX = 16.0 - maxZ;
                double nextMaxX = 16.0 - minZ;
                double nextMinZ = minX;
                double nextMaxZ = maxX;

                minX = nextMinX;
                maxX = nextMaxX;
                minZ = nextMinZ;
                maxZ = nextMaxZ;
            }

            return new Box(minX, this.minY, minZ, maxX, this.maxY, maxZ);
        }
    }
}

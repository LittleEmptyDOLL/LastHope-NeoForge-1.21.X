package com.github.littleemptydoll.lasthope.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;

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
        if (minX < 0 || minY < 0 || minZ < 0
                || maxX > 16 || maxY > 16 || maxZ > 16
                || minX > maxX || minY > maxY || minZ > maxZ) {
            throw new IllegalArgumentException("Block shape must be inside 0..16");
        }

        return new BlockShape(List.of(
                new Box(minX, minY, minZ, maxX, maxY, maxZ)
        ));
    }

    public BlockShape add(BlockShape shape) {
        List<Box> result = new ArrayList<>(boxes);
        result.addAll(shape.boxes);
        return new BlockShape(result);
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

    private record Box(
            double minX,
            double minY,
            double minZ,
            double maxX,
            double maxY,
            double maxZ
    ) {
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

            return new Box(minX, minY, minZ, maxX, maxY, maxZ);
        }
    }
}

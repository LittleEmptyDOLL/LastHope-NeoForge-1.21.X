package com.github.littleemptydoll.lasthope.block;

public final class BlockPlacement {
    private final BlockRotation rotation;

    private BlockPlacement(BlockRotation rotation) {
        this.rotation = rotation;
    }

    public static BlockPlacement of(BlockRotation rotation) {
        return new BlockPlacement(rotation);
    }

    public static BlockPlacement none() {
        return of(BlockRotation.NONE);
    }

    public static BlockPlacement horizontal() {
        return of(BlockRotation.HORIZONTAL);
    }

    public BlockRotation rotation() {
        return rotation;
    }
}

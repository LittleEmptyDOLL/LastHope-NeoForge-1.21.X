package com.github.littleemptydoll.lasthope.client.model;

import com.github.littleemptydoll.lasthope.registry.definition.BlockDefinition;
import com.github.littleemptydoll.lasthope.registry.definition.MultiBlockDefinition;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelBuilder.FaceRotation;
import net.neoforged.neoforge.client.model.generators.ModelFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Generates one local block model for every occupied multiblock cell from one
 * Blockbench-exported Minecraft model.
 */
public final class MultiBlockModelGenerator {
    private MultiBlockModelGenerator() {
    }

    public static Map<Integer, ModelFile> generate(
            BlockStateProvider provider,
            BlockDefinition definition
    ) {
        MultiBlockDefinition multiBlock = definition.multiBlock();
        if (multiBlock == null) {
            return Map.of();
        }

        JsonObject source = loadSourceModel(definition);
        Map<Integer, ModelFile> models = new LinkedHashMap<>();

        for (int part : multiBlock.occupiedParts()) {
            BlockModelBuilder model = provider.models().getBuilder(
                    AssetPaths.getBlockModelPath(definition, "part_" + part)
            );

            copyModelMetadata(model, source);
            generatePart(model, source, multiBlock, part);
            models.put(part, model);
        }

        return models;
    }

    private static JsonObject loadSourceModel(BlockDefinition definition) {
        String resource = "assets/lasthope/models/" +
                AssetPaths.getMultiBlockSourceModelPath(definition) + ".json";

        ClassLoader classLoader = MultiBlockModelGenerator.class.getClassLoader();
        try (InputStream stream = classLoader.getResourceAsStream(resource)) {
            if (stream == null) {
                throw new IllegalStateException(
                        "Missing multiblock source model: " + resource
                );
            }

            try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                JsonElement json = JsonParser.parseReader(reader);
                if (!json.isJsonObject()) {
                    throw new IllegalStateException(
                            "Multiblock source model must be a JSON object: " + resource
                    );
                }
                return json.getAsJsonObject();
            }
        } catch (IOException exception) {
            throw new UncheckedIOException(
                    "Unable to read multiblock source model: " + resource,
                    exception
            );
        }
    }

    private static void copyModelMetadata(BlockModelBuilder model, JsonObject source) {
        if (source.has("ambientocclusion")) {
            model.ao(source.get("ambientocclusion").getAsBoolean());
        }

        if (source.has("render_type")) {
            model.renderType(source.get("render_type").getAsString());
        }

        if (source.has("textures")) {
            JsonObject textures = source.getAsJsonObject("textures");
            for (Map.Entry<String, JsonElement> entry : textures.entrySet()) {
                if (entry.getValue().isJsonPrimitive()) {
                    model.texture(entry.getKey(), entry.getValue().getAsString());
                }
            }
        }
    }

    private static void generatePart(
            BlockModelBuilder model,
            JsonObject source,
            MultiBlockDefinition definition,
            int part
    ) {
        if (!source.has("elements")) {
            return;
        }

        JsonArray elements = source.getAsJsonArray("elements");
        int cellX = definition.x(part);
        int cellY = definition.y(part);
        int cellZ = definition.z(part);

        for (JsonElement elementJson : elements) {
            addElementForCell(model, elementJson.getAsJsonObject(), cellX, cellY, cellZ);
        }
    }

    private static void addElementForCell(
            BlockModelBuilder model,
            JsonObject element,
            int cellX,
            int cellY,
            int cellZ
    ) {
        double[] from = vector(element.getAsJsonArray("from"));
        double[] to = vector(element.getAsJsonArray("to"));

        double cellMinX = cellX * 16.0;
        double cellMinY = cellY * 16.0;
        double cellMinZ = cellZ * 16.0;
        double cellMaxX = cellMinX + 16.0;
        double cellMaxY = cellMinY + 16.0;
        double cellMaxZ = cellMinZ + 16.0;

        double minX = Math.max(from[0], cellMinX);
        double minY = Math.max(from[1], cellMinY);
        double minZ = Math.max(from[2], cellMinZ);
        double maxX = Math.min(to[0], cellMaxX);
        double maxY = Math.min(to[1], cellMaxY);
        double maxZ = Math.min(to[2], cellMaxZ);

        if (minX >= maxX || minY >= maxY || minZ >= maxZ) {
            return;
        }

        boolean rotated = element.has("rotation");
        if (rotated && spansMultipleCells(from, to, cellX, cellY, cellZ)) {
            throw new IllegalStateException(
                    "A rotated Blockbench element crosses a multiblock cell boundary. " +
                    "Split the element in Blockbench or keep the rotation inside one cell."
            );
        }

        var builder = model.element()
                .from(
                        (float) (minX - cellMinX),
                        (float) (minY - cellMinY),
                        (float) (minZ - cellMinZ)
                )
                .to(
                        (float) (maxX - cellMinX),
                        (float) (maxY - cellMinY),
                        (float) (maxZ - cellMinZ)
                );

        if (element.has("shade")) {
            builder.shade(element.get("shade").getAsBoolean());
        }

        if (rotated) {
            JsonObject rotation = element.getAsJsonObject("rotation");
            double[] origin = vector(rotation.getAsJsonArray("origin"));
            builder.rotation()
                    .origin(
                            (float) (origin[0] - cellMinX),
                            (float) (origin[1] - cellMinY),
                            (float) (origin[2] - cellMinZ)
                    )
                    .axis(axis(rotation.get("axis").getAsString()))
                    .angle(rotation.get("angle").getAsFloat())
                    .rescale(rotation.has("rescale") && rotation.get("rescale").getAsBoolean());
        }

        if (element.has("faces")) {
            JsonObject faces = element.getAsJsonObject("faces");
            for (Map.Entry<String, JsonElement> entry : faces.entrySet()) {
                Direction direction = direction(entry.getKey());
                if (direction == null || !keepsOriginalFace(
                        direction,
                        from,
                        to,
                        minX,
                        minY,
                        minZ,
                        maxX,
                        maxY,
                        maxZ
                )) {
                    continue;
                }

                JsonObject face = entry.getValue().getAsJsonObject();
                var faceBuilder = builder.face(direction);

                if (face.has("texture")) {
                    faceBuilder.texture(face.get("texture").getAsString());
                }

                if (face.has("cullface")) {
                    Direction cullFace = direction(face.get("cullface").getAsString());
                    if (cullFace != null) {
                        faceBuilder.cullface(cullFace);
                    }
                }

                if (face.has("tintindex")) {
                    faceBuilder.tintindex(face.get("tintindex").getAsInt());
                }

                if (face.has("rotation")) {
                    faceBuilder.rotation(faceRotation(face.get("rotation").getAsInt()));
                }

                if (face.has("uv")) {
                    double[] uv = vector4(face.getAsJsonArray("uv"));
                    double[] clippedUv = clipUv(
                            direction,
                            uv,
                            from,
                            to,
                            minX,
                            minY,
                            minZ,
                            maxX,
                            maxY,
                            maxZ
                    );
                    faceBuilder.uvs(
                            (float) clippedUv[0],
                            (float) clippedUv[1],
                            (float) clippedUv[2],
                            (float) clippedUv[3]
                    );
                }

                faceBuilder.end();
            }
        }
    }

    private static boolean spansMultipleCells(
            double[] from,
            double[] to,
            int cellX,
            int cellY,
            int cellZ
    ) {
        return from[0] < cellX * 16.0 || to[0] > (cellX + 1) * 16.0
                || from[1] < cellY * 16.0 || to[1] > (cellY + 1) * 16.0
                || from[2] < cellZ * 16.0 || to[2] > (cellZ + 1) * 16.0;
    }

    private static boolean keepsOriginalFace(
            Direction direction,
            double[] from,
            double[] to,
            double minX,
            double minY,
            double minZ,
            double maxX,
            double maxY,
            double maxZ
    ) {
        return switch (direction) {
            case DOWN -> nearlyEqual(from[1], minY);
            case UP -> nearlyEqual(to[1], maxY);
            case NORTH -> nearlyEqual(from[2], minZ);
            case SOUTH -> nearlyEqual(to[2], maxZ);
            case WEST -> nearlyEqual(from[0], minX);
            case EAST -> nearlyEqual(to[0], maxX);
        };
    }

    private static double[] clipUv(
            Direction direction,
            double[] uv,
            double[] from,
            double[] to,
            double minX,
            double minY,
            double minZ,
            double maxX,
            double maxY,
            double maxZ
    ) {
        double uMin;
        double uMax;
        double vMin;
        double vMax;
        double clippedUMin;
        double clippedUMax;
        double clippedVMin;
        double clippedVMax;

        switch (direction) {
            case NORTH, SOUTH -> {
                uMin = from[0];
                uMax = to[0];
                vMin = from[1];
                vMax = to[1];
                clippedUMin = minX;
                clippedUMax = maxX;
                clippedVMin = minY;
                clippedVMax = maxY;
            }
            case EAST, WEST -> {
                uMin = from[2];
                uMax = to[2];
                vMin = from[1];
                vMax = to[1];
                clippedUMin = minZ;
                clippedUMax = maxZ;
                clippedVMin = minY;
                clippedVMax = maxY;
            }
            case UP, DOWN -> {
                uMin = from[0];
                uMax = to[0];
                vMin = from[2];
                vMax = to[2];
                clippedUMin = minX;
                clippedUMax = maxX;
                clippedVMin = minZ;
                clippedVMax = maxZ;
            }
            default -> throw new IllegalStateException("Unexpected direction: " + direction);
        }

        double u1 = interpolate(uv[0], uv[2], uMin, uMax, clippedUMin);
        double u2 = interpolate(uv[0], uv[2], uMin, uMax, clippedUMax);
        double v1 = interpolate(uv[1], uv[3], vMin, vMax, clippedVMin);
        double v2 = interpolate(uv[1], uv[3], vMin, vMax, clippedVMax);
        return new double[]{u1, v1, u2, v2};
    }

    private static double interpolate(double uvMin, double uvMax, double min, double max, double value) {
        if (nearlyEqual(min, max)) {
            return uvMin;
        }
        double ratio = (value - min) / (max - min);
        return uvMin + (uvMax - uvMin) * ratio;
    }

    private static Direction.Axis axis(String value) {
        return switch (value) {
            case "x" -> Direction.Axis.X;
            case "y" -> Direction.Axis.Y;
            case "z" -> Direction.Axis.Z;
            default -> throw new IllegalArgumentException("Unknown model rotation axis: " + value);
        };
    }

    private static FaceRotation faceRotation(int degrees) {
        return switch (Math.floorMod(degrees, 360)) {
            case 0 -> FaceRotation.ZERO;
            case 90 -> FaceRotation.CLOCKWISE_90;
            case 180 -> FaceRotation.UPSIDE_DOWN;
            case 270 -> FaceRotation.COUNTERCLOCKWISE_90;
            default -> throw new IllegalArgumentException(
                    "Block model face rotation must be 0, 90, 180 or 270: " + degrees
            );
        };
    }

    private static Direction direction(String value) {
        return switch (value) {
            case "down" -> Direction.DOWN;
            case "up" -> Direction.UP;
            case "north" -> Direction.NORTH;
            case "south" -> Direction.SOUTH;
            case "west" -> Direction.WEST;
            case "east" -> Direction.EAST;
            default -> null;
        };
    }

    private static double[] vector(JsonArray array) {
        return new double[]{
                array.get(0).getAsDouble(),
                array.get(1).getAsDouble(),
                array.get(2).getAsDouble()
        };
    }

    private static double[] vector4(JsonArray array) {
        return new double[]{
                array.get(0).getAsDouble(),
                array.get(1).getAsDouble(),
                array.get(2).getAsDouble(),
                array.get(3).getAsDouble()
        };
    }

    private static boolean nearlyEqual(double first, double second) {
        return Math.abs(first - second) < 0.0001;
    }
}

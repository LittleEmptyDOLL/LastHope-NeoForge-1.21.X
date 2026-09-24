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

        for (JsonElement elementJson : elements) {
            JsonObject element = elementJson.getAsJsonObject();
            int ownerPart = findOwningPart(element, definition);
            if (ownerPart == part) {
                addElement(model, element, definition.x(part), definition.y(part), definition.z(part));
            }
        }
    }

    /**
     * Assigns the whole model element to the occupied cell containing the
     * largest volume of its model-space bounding box. This keeps each element
     * intact instead of clipping it at cell boundaries, which is important for
     * rotated elements and avoids visual seams.
     *
     * Ties are resolved by the lowest PART index for deterministic generation.
     */
    private static int findOwningPart(
            JsonObject element,
            MultiBlockDefinition definition
    ) {
        double[] bounds = elementBounds(element);

        int bestPart = -1;
        double bestVolume = -1.0;

        for (int part : definition.occupiedParts()) {
            int cellX = definition.x(part);
            int cellY = definition.y(part);
            int cellZ = definition.z(part);

            double cellMinX = cellX * 16.0;
            double cellMinY = cellY * 16.0;
            double cellMinZ = cellZ * 16.0;
            double cellMaxX = cellMinX + 16.0;
            double cellMaxY = cellMinY + 16.0;
            double cellMaxZ = cellMinZ + 16.0;

            double intersection = intersectionVolume(
                    bounds,
                    cellMinX, cellMinY, cellMinZ,
                    cellMaxX, cellMaxY, cellMaxZ
            );

            if (intersection > bestVolume
                    || (nearlyEqual(intersection, bestVolume) && (bestPart < 0 || part < bestPart))) {
                bestVolume = intersection;
                bestPart = part;
            }
        }

        if (bestPart < 0) {
            throw new IllegalStateException("Multiblock model element does not intersect any occupied cell.");
        }

        return bestPart;
    }

    /**
     * Returns an axis-aligned bounding box for the model element. For rotated
     * elements the bounds are calculated from the eight rotated corners, so
     * the ownership decision uses the element's actual rotated extent.
     */
    private static double[] elementBounds(JsonObject element) {
        double[] from = vector(element.getAsJsonArray("from"));
        double[] to = vector(element.getAsJsonArray("to"));

        if (!element.has("rotation")) {
            return new double[]{
                    from[0], from[1], from[2],
                    to[0], to[1], to[2]
            };
        }

        JsonObject rotation = element.getAsJsonObject("rotation");
        double[] origin = vector(rotation.getAsJsonArray("origin"));
        Direction.Axis axis = axis(rotation.get("axis").getAsString());
        double angle = Math.toRadians(rotation.get("angle").getAsDouble());

        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        double maxZ = Double.NEGATIVE_INFINITY;

        for (int x = 0; x <= 1; x++) {
            for (int y = 0; y <= 1; y++) {
                for (int z = 0; z <= 1; z++) {
                    double px = x == 0 ? from[0] : to[0];
                    double py = y == 0 ? from[1] : to[1];
                    double pz = z == 0 ? from[2] : to[2];

                    double[] rotated = rotatePoint(
                            px, py, pz, origin, axis, angle
                    );

                    minX = Math.min(minX, rotated[0]);
                    minY = Math.min(minY, rotated[1]);
                    minZ = Math.min(minZ, rotated[2]);
                    maxX = Math.max(maxX, rotated[0]);
                    maxY = Math.max(maxY, rotated[1]);
                    maxZ = Math.max(maxZ, rotated[2]);
                }
            }
        }

        return new double[]{minX, minY, minZ, maxX, maxY, maxZ};
    }

    private static double[] rotatePoint(
            double x,
            double y,
            double z,
            double[] origin,
            Direction.Axis axis,
            double angle
    ) {
        double dx = x - origin[0];
        double dy = y - origin[1];
        double dz = z - origin[2];

        double sin = Math.sin(angle);
        double cos = Math.cos(angle);

        double rotatedX;
        double rotatedY;
        double rotatedZ;

        switch (axis) {
            case X -> {
                rotatedX = dx;
                rotatedY = dy * cos - dz * sin;
                rotatedZ = dy * sin + dz * cos;
            }
            case Y -> {
                rotatedX = dx * cos + dz * sin;
                rotatedY = dy;
                rotatedZ = -dx * sin + dz * cos;
            }
            case Z -> {
                rotatedX = dx * cos - dy * sin;
                rotatedY = dx * sin + dy * cos;
                rotatedZ = dz;
            }
            default -> throw new IllegalStateException("Unexpected rotation axis: " + axis);
        }

        return new double[]{
                rotatedX + origin[0],
                rotatedY + origin[1],
                rotatedZ + origin[2]
        };
    }

    private static double intersectionVolume(
            double[] bounds,
            double cellMinX,
            double cellMinY,
            double cellMinZ,
            double cellMaxX,
            double cellMaxY,
            double cellMaxZ
    ) {
        double minX = Math.max(bounds[0], cellMinX);
        double minY = Math.max(bounds[1], cellMinY);
        double minZ = Math.max(bounds[2], cellMinZ);
        double maxX = Math.min(bounds[3], cellMaxX);
        double maxY = Math.min(bounds[4], cellMaxY);
        double maxZ = Math.min(bounds[5], cellMaxZ);

        if (minX >= maxX || minY >= maxY || minZ >= maxZ) {
            return 0.0;
        }

        return (maxX - minX) * (maxY - minY) * (maxZ - minZ);
    }

    private static void addElement(
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

        var builder = model.element()
                .from(
                        (float) (from[0] - cellMinX),
                        (float) (from[1] - cellMinY),
                        (float) (from[2] - cellMinZ)
                )
                .to(
                        (float) (to[0] - cellMinX),
                        (float) (to[1] - cellMinY),
                        (float) (to[2] - cellMinZ)
                );

        if (element.has("shade")) {
            builder.shade(element.get("shade").getAsBoolean());
        }

        if (element.has("rotation")) {
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
                if (direction == null) {
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
                    faceBuilder.uvs(
                            (float) uv[0],
                            (float) uv[1],
                            (float) uv[2],
                            (float) uv[3]
                    );
                }

                faceBuilder.end();
            }
        }
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

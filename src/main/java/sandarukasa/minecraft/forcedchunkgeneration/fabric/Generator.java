package sandarukasa.minecraft.forcedchunkgeneration.fabric;

import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class Generator {
    /**
     * Ensures that all the chunks at specified positions have been generated
     *
     * @param serverChunkManager chunk manager of the server's dimension where chunks need to be generated
     * @param chunkPositions     stream of chunk positions to process
     * @return the number of processed chunks
     */
    public static int generateChunks(@NotNull ServerChunkManager serverChunkManager,
                                     @NotNull Stream<@NotNull ChunkPos> chunkPositions) {
        var result = new AtomicInteger();
        chunkPositions.forEach(chunkPos -> {
            serverChunkManager.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL, true);
            result.getAndIncrement();
        });
        return result.get();
    }

    /**
     * Ensures that all the chunks in the specified rectangular area have been generated.
     * The area is specified by providing chunk positions of its two opposite corners.
     *
     * @param serverChunkManager chunk manager of the server's dimension where chunks need to be generated
     * @param corner1            chunk position of the first corner
     * @param corner2            chunk position of the second corner
     * @return the number of processed chunks
     */
    public static int generateChunks(@NotNull ServerChunkManager serverChunkManager,
                                     @NotNull ChunkPos corner1, @NotNull ChunkPos corner2) {
        return generateChunks(serverChunkManager, ChunkPos.stream(corner1, corner2));
    }

    /**
     * Ensures that all the chunks in the specified rectangular area have been generated.
     * The area is specified by providing its center (as ChunkPos) and its Chebyshev radius (in chunks).
     *
     * @param serverChunkManager chunk manager of the server's dimension where chunks need to be generated
     * @param center             chunk position of the center
     * @param chunkRadius        the radius of the area, in chunks, Chebyshev distance
     * @return the number of processed chunks
     */
    public static int generateChunks(@NotNull ServerChunkManager serverChunkManager,
                                     @NotNull ChunkPos center, int chunkRadius) {
        return generateChunks(serverChunkManager, ChunkPos.stream(center, chunkRadius));
    }
}

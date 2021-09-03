package sandarukasa.minecraft.forcedchunkgeneration.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Mod implements ModInitializer {
    public static int generateChunks(ServerChunkManager serverChunkManager, BlockPos block1, BlockPos block2) {
        var result = new AtomicInteger();
        ChunkPos.stream(
                new ChunkPos(Objects.requireNonNull(block1)),
                new ChunkPos(Objects.requireNonNullElse(block2, block1))
        ).parallel().forEach(chunkPos -> {
            serverChunkManager.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL, true);
            result.getAndIncrement();
        });
        return result.get();
    }

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) ->
                dispatcher.register(
                        literal("generate").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))
                                .then(argument("coordinates", BlockPosArgumentType.blockPos())
                                        .executes(context -> generateChunks(
                                                context.getSource().getWorld().getChunkManager(),
                                                BlockPosArgumentType.getBlockPos(context, "coordinates"),
                                                null
                                                )
                                        )
                                        .then(argument("additional_coordinates", BlockPosArgumentType.blockPos())
                                                .executes(context -> generateChunks(
                                                        context.getSource().getWorld().getChunkManager(),
                                                        BlockPosArgumentType.getBlockPos(context, "coordinates"),
                                                        BlockPosArgumentType.getBlockPos(context, "additional_coordinates")
                                                        )
                                                )
                                        )
                                )
                )
        );
    }
}

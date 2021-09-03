package sandarukasa.minecraft.forcedchunkgeneration.fabric;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.ChunkPos;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Commands {
    static final String COMMAND_NAME = "generatechunks";
    static final String ARGUMENT_FIRST_CORNER = "first_corner";
    static final String ARGUMENT_SECOND_CORNER = "second_corner";
    static final String ARGUMENT_CENTER = "center";
    static final String ARGUMENT_RADIUS = "radius_in_chunks";


    static int rectangularCommand(@NotNull CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        final var first_corner = new ChunkPos(BlockPosArgumentType.getBlockPos(context, ARGUMENT_FIRST_CORNER));
        final var second_corner = new ChunkPos(BlockPosArgumentType.getBlockPos(context, ARGUMENT_SECOND_CORNER));
        var result = Generator.generateChunks(
                context.getSource().getWorld().getChunkManager(),
                first_corner,
                second_corner
        );
        context.getSource().sendFeedback(
                Text.of(
                        result == 1 ?
                                "Chunk " + first_corner + " has now been generated" :
                                result + " chunks in rectangular area from " + first_corner +
                                        " to " + second_corner + " have now been generated"
                ),
                true
        );
        return result;
    }

    static int radiusCommand(@NotNull CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        final var center = new ChunkPos(BlockPosArgumentType.getBlockPos(context, ARGUMENT_CENTER));
        final int radius = IntegerArgumentType.getInteger(context, ARGUMENT_RADIUS);
        var result = Generator.generateChunks(
                context.getSource().getWorld().getChunkManager(),
                center,
                radius
        );
        context.getSource().sendFeedback(
                Text.of(
                        result == 1 ?
                                "Chunk " + center + " has now been generated" :
                                result + " chunks in area with center at " + center + " and radius of " + radius + " have been generated"
                ),
                true
        );
        return result;
    }

    static void register(@NotNull CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        dispatcher.register(
                literal(COMMAND_NAME).requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))
                        .then(argument(ARGUMENT_FIRST_CORNER, BlockPosArgumentType.blockPos())
                                .then(argument(ARGUMENT_SECOND_CORNER, BlockPosArgumentType.blockPos())
                                        .executes(Commands::rectangularCommand)))
                        .then(argument(ARGUMENT_CENTER, BlockPosArgumentType.blockPos())
                                .then(argument(ARGUMENT_RADIUS, IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                                        .executes(Commands::radiusCommand)))
        );
    }
}

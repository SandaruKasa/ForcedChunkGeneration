package sandarukasa.minecraft.forcedchunkgeneration.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

public class Mod implements ModInitializer {
    public static void processServer(MinecraftServer server) {
        server.getWorlds().forEach(System.out::println);
    }

    @Override
    public void onInitialize() {
        System.out.println("INITIALIZATION");
        ServerLifecycleEvents.SERVER_STARTED.register(Mod::processServer);
    }
}

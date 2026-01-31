package berries.servermod.bconverter3;

import berries.servermod.bconverter3.commands.Commands;
import net.fabricmc.api.ModInitializer;

public class BlockReplacer implements ModInitializer {
    @Override
    public void onInitialize() {
        Config.INSTANCE.read();
        Config.INSTANCE.save();

        Commands.register();

        System.out.println("[BConverter] Initialized");
    }
}

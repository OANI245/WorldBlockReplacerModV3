package berries.servermod.bconverter3;

import berries.servermod.bconverter3.commands.Commands;
import berries.servermod.bconverter3.commands.ModCommand;
import net.fabricmc.api.ModInitializer;

public class BlockConverter implements ModInitializer {
    @Override
    public void onInitialize() {
        Config.INSTANCE.read();
        Config.INSTANCE.save();

        Commands.register();

        System.out.println("[BConverter] Initialized");
    }
}

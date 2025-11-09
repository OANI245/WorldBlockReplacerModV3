package berries.servermod.bconverter3.commands;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public abstract class Commands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register(
                (dispatcher, dedicated, x) -> {
                    ModCommand.getInstance().register(dispatcher);
                }
        );
    }
}

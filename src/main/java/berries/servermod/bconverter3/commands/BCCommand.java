package berries.servermod.bconverter3.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;

public interface BCCommand {
    void register(CommandDispatcher<CommandSourceStack> dispatcher);
}

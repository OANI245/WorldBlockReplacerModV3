package berries.servermod.bconverter3.commands;

import berries.servermod.bconverter3.Config;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public class ModCommand implements BCCommand {
    private ModCommand() {
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("bconverter")
                        .then(LiteralArgumentBuilder.<CommandSourceStack>literal("reload")
                                .requires(source -> source.hasPermission(4))
                                .executes((ctx) -> {
                                    Config.INSTANCE.read();
                                    Config.INSTANCE.save();
                                    ctx.getSource().sendSuccess(() -> Component.literal("BConverter is reloaded!").withStyle(ChatFormatting.GREEN), false);
                                    return 1;
                                })
                        )
        );
    }

    static BCCommand getInstance() {
        return new ModCommand();
    }
}

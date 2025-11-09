package berries.servermod.bconverter3.mixin;

import berries.servermod.bconverter3.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public class MixinLevel {
    @Inject(
            method = "getBlockState",
            at = @At("RETURN"),
            cancellable = true)
    public void getBlockState(BlockPos blockPos, CallbackInfoReturnable<BlockState> cir) {
        var blockKey = BuiltInRegistries.BLOCK.getKey(cir.getReturnValue().getBlock());
        if (Config.INSTANCE.blockConverterIds.containsKey(blockKey.toString())) {
            BlockState state = BuiltInRegistries.BLOCK.get(new ResourceLocation(Config.INSTANCE.blockConverterIds.get(blockKey.toString()))).defaultBlockState();
            cir.setReturnValue(state);
        }
    }
}

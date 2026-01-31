package berries.servermod.bconverter3.mixin;

import berries.servermod.bconverter3.Config;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Set;

@Mixin(ChunkSerializer.class)
public class MixinChunkSerializer {
    @Inject(method = "read", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/ListTag;getCompound(I)Lnet/minecraft/nbt/CompoundTag;", ordinal = 0, shift = At.Shift.AFTER))
    private static void read(ServerLevel serverLevel, PoiManager poiManager, ChunkPos chunkPos, CompoundTag compoundTag, CallbackInfoReturnable<ProtoChunk> cir, @Local CompoundTag compoundTag2) {
        CompoundTag blockStatesBlock = compoundTag2.getCompound("block_states");
        ListTag list = blockStatesBlock.getList("palette", 10);
        for (int j = 0; j < list.size(); j++) {
            CompoundTag block = list.getCompound(j);
            String id = block.getString("Name");
            String newId = Config.INSTANCE.blockConverterIds.getOrDefault(id, id);
            boolean isReplacedBlock = !newId.equals(id);
            block.putString("Name", newId);
            printDebug("[BConverter] Complete replace " + id, isReplacedBlock);
            //Properties: {facing: "north"}
            if (block.contains("Properties")) {
                CompoundTag states = block.getCompound("Properties");
                Set<String> keys = states.getAllKeys();
                CompoundTag newStates = new CompoundTag();
                printDebug("[", isReplacedBlock);
                printDebug(states, isReplacedBlock);
                printDebug("] to " + newId + "[", isReplacedBlock);
                for (String key : keys) {
                    List<Tuple<String, String>> lsba = Config.INSTANCE.blockConverterStatesNames.getOrDefault(id, null);
                    var lsbb = Config.INSTANCE.blockConverterStates.getOrDefault(id, null);
                    if (lsbb != null) {
                        boolean sign = false;
                        for (Tuple<String, Tuple<String, String>> stt : lsbb) {
                            if (key.equals(stt.getA()) && (states.getString(stt.getA()).equals(stt.getB().getA()) || stt.getB().getA().isEmpty())) {
                                newStates.putString(stt.getA(), stt.getB().getB());
                                sign = true;
                                break;
                            }
                        }
                        if (!sign) {
                            newStates.put(key, states.get(key));
                        }
                    }
                    if (lsba != null) {
                        boolean sign = false;
                        for (Tuple<String, String> sst : lsba) {
                            if (key.equals(sst.getA())) {
                                newStates.put(sst.getB(), states.get(key));
                                sign = true;
                                break;
                            }
                        }
                        if (!sign) {
                            newStates.put(key, states.get(key));
                        }
                    } else {
                        newStates = states;
                    }
                }
                printDebug(newStates, isReplacedBlock);
                printDebug("]", isReplacedBlock);
                block.put("Properties", newStates);
            } else {
                printDebug(" to " + newId, isReplacedBlock);
            }
            printDebug(isReplacedBlock);
            list.set(j, block);
        }
        blockStatesBlock.put("palette", list);
        compoundTag2.put("block_states", blockStatesBlock);
    }

    @Unique
    private static void printDebug(String str, boolean bl) {
        if (Config.INSTANCE.showDebug && bl) {
            System.out.print(str);
        }
    }

    @Unique
    private static void printDebug(CompoundTag list, boolean bl) {
        if (Config.INSTANCE.showDebug && bl) {
            String[] keys = list.getAllKeys().toArray(new String[0]);
            for (int i = 0; i < keys.length; i++) {
                printDebug(keys[i] + "=" + list.get(keys[i]), bl);
                if (i < keys.length - 1) {
                    printDebug(",", bl);
                }
            }
        }
    }

    @Unique
    private static void printDebug(boolean bl) {
        if (Config.INSTANCE.showDebug && bl) {
            System.out.println();
        }
    }
}

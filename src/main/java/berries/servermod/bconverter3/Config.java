package berries.servermod.bconverter3;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Tuple;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {
    public static final Config INSTANCE = new Config();
    public static final String CONFIG_PATH = (
            FabricLoader.getInstance().getConfigDir().toString()
            + File.separator + "bconverter3.json"
    );

    public final Map<String, String> blockConverterIds = new HashMap<>(Map.of("bconverter:test_block", "minecraft:pink_wool"));
    public final Map<String, List<Tuple<String, String>>> blockConverterStatesNames = new HashMap<>(Map.of("bconverter:test_block", List.of(new Tuple<>("bef", "aft"))));
    public final Map<Object, List<Tuple<String, Tuple<String, String>>>> blockConverterStates = new HashMap<>(Map.of("bconverter:test_block", List.of(new Tuple<>("def", new Tuple<>("bef", "aft")))));
    public boolean showDebug = false;

    private Config() {}

    @SuppressWarnings("unused")
    public void read() {
        File file = new File(CONFIG_PATH);
        if (!file.exists()) {
            save();
        }

        try {
            JsonObject root = JsonParser.parseReader(new BufferedReader(new FileReader(file))).getAsJsonObject();
            if (root.has("replace_blocks") && root.get("replace_blocks").isJsonArray()) {
                blockConverterIds.clear();
                JsonArray replaceBlocks = root.getAsJsonArray("replace_blocks");
                for (JsonElement element : replaceBlocks) {
                    JsonObject block = element.getAsJsonObject();
                    if (block.has("before") && block.has("after")) {
                        String before = block.get("before").getAsString();
                        String after = block.get("after").getAsString();

                        blockConverterIds.put(before, after);

                        //BlockStates Processor...
                        if (block.has("state_ids")) {
                            blockConverterStatesNames.clear();
                            JsonArray stateIdsArray = block.getAsJsonArray("state_ids");
                            List<Tuple<String, String>> stateReplaceIds = new ArrayList<>();
                            for (JsonElement jsonElement : stateIdsArray) {
                                JsonObject stateIdsBlock = jsonElement.getAsJsonObject();
                                if (stateIdsBlock.has("before") && stateIdsBlock.has("after")) {
                                    String sBefore = stateIdsBlock.get("before").getAsString();
                                    String sAfter = stateIdsBlock.get("after").getAsString();

                                    stateReplaceIds.add(new Tuple<>(sBefore, sAfter));
                                }
                            }

                            blockConverterStatesNames.put(before, stateReplaceIds);
                        }

                        if (block.has("state_values")) {
                            blockConverterStates.clear();
                            JsonArray stateIdsArray = block.getAsJsonArray("state_values");
                            List<Tuple<String, Tuple<String, String>>> stateReplaceValues = new ArrayList<>();
                            for (JsonElement jsonElement : stateIdsArray) {
                                JsonObject stateIdsBlock = jsonElement.getAsJsonObject();
                                if (stateIdsBlock.has("key") && stateIdsBlock.has("replace")) {
                                    String sBefore = stateIdsBlock.get("key").getAsString();
                                    JsonObject aAfter = stateIdsBlock.get("replace").getAsJsonObject();
                                    if (aAfter.size() != 2) {
                                        continue;
                                    }

                                    stateReplaceValues.add(new Tuple<>(sBefore, new Tuple<>(aAfter.get("before").getAsString(), aAfter.get("after").getAsString())));
                                }
                            }

                            blockConverterStates.put(before, stateReplaceValues);
                        }
                    } else {
                        continue;
                    }
                }
            }
            if (root.has("show_debug")) {
                showDebug = root.get("show_debug").getAsBoolean();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    public boolean save() {
        File file = new File(CONFIG_PATH);
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        JsonObject root = new JsonObject();
        JsonArray replaceBlocks = new JsonArray();
        blockConverterIds.forEach((bef, aft) -> {
            JsonObject block = new JsonObject();
            block.addProperty("before", bef);
            block.addProperty("after", aft);
            JsonArray stateIds = new JsonArray();
            JsonArray stateValues = new JsonArray();
            blockConverterStatesNames.getOrDefault(bef, new ArrayList<>(0)).forEach((befaaft) -> {
                JsonObject block1 = new JsonObject();
                block1.addProperty("before", befaaft.getA());
                block1.addProperty("after", befaaft.getB());
                stateIds.add(block1);
            });
            blockConverterStates.getOrDefault(bef, new ArrayList<>(0)).forEach((befaaft) -> {
                JsonObject block2 = new JsonObject();
                block2.addProperty("key", befaaft.getA());
                JsonObject object = new JsonObject();
                object.addProperty("before", befaaft.getB().getA());
                object.addProperty("after", befaaft.getB().getB());
                block2.add("replace", object);
                stateValues.add(block2);
            });
            block.add("state_values", stateValues);
            block.add("state_ids", stateIds);
            replaceBlocks.add(block);
        });
        //Root Members and Save
        root.add("replace_blocks", replaceBlocks);
        root.addProperty("show_debug", showDebug);

        String json = root.toString();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(json);
            writer.flush();
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}

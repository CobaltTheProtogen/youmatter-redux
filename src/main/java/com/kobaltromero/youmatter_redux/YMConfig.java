package com.kobaltromero.youmatter_redux;

import java.util.List;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

public class YMConfig {

    public static final ModConfigSpec CONFIG_SPEC;
    public static final YMConfig CONFIG;

    public final ModConfigSpec.ConfigValue<List<? extends String>> filterItems;
    public final ModConfigSpec.BooleanValue filterMode;
    public final ModConfigSpec.ConfigValue<List<? extends String>> overrides;
    public final ModConfigSpec.ConfigValue<Integer> defaultAmount;
    public final ModConfigSpec.ConfigValue<Integer> defaultScans;

    public final ModConfigSpec.ConfigValue<Integer> energyReplicator;
    public final ModConfigSpec.ConfigValue<Integer> energyEncoder;
    public final ModConfigSpec.ConfigValue<Integer> energyScanner;

    public final ModConfigSpec.ConfigValue<Integer> productionPerTick;

    static {
        Pair<YMConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(YMConfig::new);
        CONFIG_SPEC = specPair.getRight();
        CONFIG = specPair.getLeft();
    }

    YMConfig(ModConfigSpec.Builder builder) {
        filterMode = builder
                .comment("Use the filterItems list as blacklist (true) or as whitelist (false). Whitelist means, that you can only duplicate those items in that list. Blacklist is vice-versa.")
                .define("filterMode", true);
        filterItems = builder
                .comment("List of items that are being treated specially. See filterMode for further details. Format: \"modid:item\"")
                .defineListAllowEmpty("filterItems", Lists.newArrayList("youmatter:black_hole", "youmatter:umatter_bucket", "youmatter:stabilizer_bucket"), e -> e instanceof String && ((String) e).contains(":"));
        overrides = builder
                .comment("Overrides: Set your desired required U-Matter values and required scans for each item. These do not apply when you e.g. have whitelist on but it doesn't include the desired override. Format: \"modid:item|amount,scans\"")
                .defineListAllowEmpty("overrides", Lists.newArrayList("minecraft:diamond|2500,16", "minecraft:nether_star|5000,16"), e -> e instanceof String && ((String) e).contains(":") && ((String) e).contains("|") && ((String) e).contains(","));
        defaultAmount = builder
                .comment("The default amount that is required to duplicate an item if it is not overridden.")
                .define("defaultAmount", 1000);
        defaultScans = builder
                .comment("The default scans required for the encoder to encode an item to a Thumb Drive.")
                .define("defaultScans", 8);
        energyReplicator = builder
                .comment("The energy consumption of the replicator per tick. Default: 2048")
                .define("energyReplicator", 2048);
        energyEncoder = builder
                .comment("The energy consumption of the encoder per tick. Default: 512")
                .define("energyEncoder", 512);
        energyScanner = builder
                .comment("The energy consumption of the scanner per tick. Default: 512")
                .define("energyScanner", 512);
        productionPerTick = builder
                .comment("Determines how much U-Matter [in mB] the creator produces every work cycle. Energy is withdrawn like this: if energy more than 30% of max energy, consume 30% and add [whatever value below] of U-Matter to the tank. Default is 1mB/work cycle. Don't increase this too much due to balancing issues.")
                .define("productionPerTick", 1);
    }

    public Object[] getOverride(String registryName) {
        for (String s : overrides.get()) {
            String[] parts = s.split("[|,]", 3);
            String foundName = parts[0];
            String foundValue = parts.length > 1 ? parts[1] : "";
            String foundValue2 = parts.length > 2 ? parts[2] : "";

            if (foundName.equalsIgnoreCase(registryName)) {
                return new Object[]{foundName, foundValue, foundValue2};
            }
        }
        return null;
    }
}

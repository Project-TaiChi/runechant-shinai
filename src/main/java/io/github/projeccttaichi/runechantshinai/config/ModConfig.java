package io.github.projeccttaichi.runechantshinai.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ModConfig {

    public static final ModConfig CONFIG;
    public static final ModConfigSpec CONFIG_SPEC;

    private ModConfig(ModConfigSpec.Builder builder) {

    }

    //CONFIG and CONFIG_SPEC are both built from the same builder, so we use a static block to seperate the properties
    static {
        Pair<ModConfig, ModConfigSpec> pair =
                new ModConfigSpec.Builder().configure(ModConfig::new);

        //Store the resulting values
        CONFIG = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }

}

package io.github.projeccttaichi.runechantshinai.init;

import io.github.projeccttaichi.runechantshinai.constants.Ids;
import io.github.projeccttaichi.runechantshinai.magic.record.BaseRecord;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Supplier;

import static io.github.projeccttaichi.runechantshinai.constants.Locations.modLoc;

public class ModRecords {

    public static final ResourceKey<Registry<BaseRecord>> RECORD_REGISTRY_KEY = ResourceKey.createRegistryKey(modLoc("records"));
    public static final Registry<BaseRecord> RECORD_REGISTRY = new RegistryBuilder<>(RECORD_REGISTRY_KEY)
            // If you want to enable integer id syncing, for networking.
            // These should only be used in networking contexts, for example in packets or purely networking-related NBT data.
            .sync(true)
            // The default key. Similar to minecraft:air for blocks. This is optional.
            .defaultKey(modLoc("empty"))
            // Build the registry.
            .create();


    private static void registerRegistries(NewRegistryEvent event) {
        event.register(RECORD_REGISTRY);
    }

    public static void init(IEventBus eventBus) {
        eventBus.addListener(ModRecords::registerRegistries);
        RECORDS.register(eventBus);


        // generate 100 test records
        for (int i = 0; i < 100; i++) {
            RECORDS.register("test_" + i, BaseRecord::new);
        }
    }

    public static final DeferredRegister<BaseRecord> RECORDS = DeferredRegister.create(RECORD_REGISTRY, Ids.MOD_ID);
    public static final Supplier<BaseRecord> SPELL_FIRE_BALL = RECORDS.register("fire_ball", BaseRecord::new);
    public static final Supplier<BaseRecord> SPELL_WATER_FLOW = RECORDS.register("water_flow", BaseRecord::new);

}
